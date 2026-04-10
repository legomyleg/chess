package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import exception.BadRequestException;
import exception.DatabaseErrorException;
import exception.NotAuthenticatedException;
import exception.ResponseException;
import io.javalin.websocket.WsMessageContext;
import model.GameData;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;
import java.util.ArrayList;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;

public class WebSocketService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public WebSocketService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public void validateAuthToken(String authToken) throws DatabaseErrorException, NotAuthenticatedException {
        if (authToken == null) {
            throw new NotAuthenticatedException();
        }

        try {
            if (authDAO.getAuthByToken(authToken) == null) {
                throw new NotAuthenticatedException();
            }
        } catch (DataAccessException e) {
            throw new DatabaseErrorException("Could not connect with database.");
        }
    }

    public void validateGameExists(Integer gameID) throws BadRequestException, DatabaseErrorException {
        if (gameID == null) {
            throw new BadRequestException();
        }
        try {
            if (gameDAO.getGameByGameID(gameID) == null) {
                throw new BadRequestException("Invalid game ID.");
            }
        } catch (DataAccessException e) {
            throw new DatabaseErrorException("Database access encountered an error.");
        }
    }

    public String getUsername(String authToken) throws DatabaseErrorException {
        try {
            return authDAO.getAuthByToken(authToken).username();
        } catch (DataAccessException e) {
            throw new DatabaseErrorException("Could not validate authentication.");
        }
    }

    public boolean isPlayerInGame(Integer gameID, String authToken) throws DatabaseErrorException {
        try {
            var gameData = gameDAO.getGameByGameID(gameID);
            var username = getUsername(authToken);
            return username.equals(gameData.whiteUsername()) || username.equals(gameData.blackUsername());
        } catch (DataAccessException e) {
            throw new DatabaseErrorException("Could not complete request due to a database error.");
        }
    }

    public GameData getGameData(Integer gameID) throws DatabaseErrorException {
        try {
            return gameDAO.getGameByGameID(gameID);
        } catch(DataAccessException e) {
            throw new DatabaseErrorException("Could not retrieve game data.");
        }
    }

    public void connect(WsMessageContext ctx, UserGameCommand command, ConnectionManager connectionManager) throws ResponseException {
        assert command.getCommandType() == UserGameCommand.CommandType.CONNECT;

        var commandContext = requireCommandContext(command);
        boolean observing = !isPlayerInGame(commandContext.gameID(), commandContext.authToken());

        connectionManager.addSessionToGame(commandContext.gameID(), ctx.session);

        ctx.send(new LoadGameMessage(getGameData(commandContext.gameID()).game()).toString());

        String role = observing ? "an observer" : "a player";
        var notification = new NotificationMessage(commandContext.username() + " joined the game as " + role + ".");
        try {
            connectionManager.broadcastMessageToGame(commandContext.gameID(), ctx.session, notification);
        } catch (IOException e) {
            throw new ResponseException(500, "Server suffered an error.");
        }
    }

    public void leave(WsMessageContext ctx, UserGameCommand command, ConnectionManager connectionManager) throws ResponseException {
        assert command.getCommandType().equals(UserGameCommand.CommandType.LEAVE);

        var commandContext = requireCommandContext(command);

        if (isPlayerInGame(commandContext.gameID(), commandContext.authToken())){
            GameData gameData;
            try {
                gameData = gameDAO.getGameByGameID(command.getGameID());
                ChessGame.TeamColor playerColor = getPlayerColor(gameData, commandContext.username());
                if (playerColor == WHITE) {
                    gameDAO.updateWhitePlayer(command.getGameID(), null);
                } else {
                    gameDAO.updateBlackPlayer(command.getGameID(), null);
                }
            } catch (DataAccessException e) {
                throw new ResponseException(500, "Server encountered an error.");
            }
        }

        connectionManager.removeSessionFromGame(commandContext.gameID(), ctx.session);

        var notification = new NotificationMessage(commandContext.username() + " has left the game.");

        try {
            connectionManager.broadcastMessageToGame(commandContext.gameID(), null, notification);
        } catch (IOException e) {
            throw new ResponseException(500, "Error: Server could not broadcast notification.");
        }
    }

    public void resign(WsMessageContext ctx, UserGameCommand command, ConnectionManager connectionManager) throws ResponseException {
        assert command.getCommandType().equals(UserGameCommand.CommandType.RESIGN);

        var commandContext = requireCommandContext(command);

        if (!isPlayerInGame(commandContext.gameID(), commandContext.authToken())) {
            sendError(ctx, "Observers cannot resign.");
            return;
        }

        try {
            if (!gameFull(command.getGameID())) {
                sendError(ctx, "Cannot resign, game has not yet started.");
                return;
            }
            if (!gameDAO.getGameByGameID(command.getGameID()).game().inProgress()) {
                sendError(ctx, "Cannot resign. Game over.");
                return;
            }

            gameDAO.endGame(command.getGameID());
            var resignNotification = new NotificationMessage(commandContext.username() +  " resigned. Lame.");
            connectionManager.broadcastMessageToGame(command.getGameID(), null, resignNotification);
        } catch (DataAccessException | IOException e) {
            sendError(ctx, "Server error.");
        }
    }

    private boolean gameFull(Integer gameID) throws DataAccessException {
        var gameData = gameDAO.getGameByGameID(gameID);
        return gameData.whiteUsername() != null && gameData.blackUsername() != null;
    }


    public void makeMove(WsMessageContext ctx, MakeMoveCommand command, ConnectionManager connectionManager) {
        var moveContext = loadMoveContext(ctx, command);
        if (moveContext == null) {
            return;
        }

        if (!applyMove(ctx, moveContext, command.getMove())) {
            return;
        }

        broadcastMoveResults(ctx, command.getMove(), moveContext, connectionManager);
    }

    private CommandContext requireCommandContext(UserGameCommand command) throws NotAuthenticatedException,
            DatabaseErrorException,
            BadRequestException {
        String authToken = command.getAuthToken();
        Integer gameID = command.getGameID();

        String username;
        validateAuthToken(authToken);
        validateGameExists(gameID);
        username = getUsername(authToken);

        return new CommandContext(authToken, gameID, username);
    }

    private MoveContext loadMoveContext(WsMessageContext ctx, MakeMoveCommand command) {
        try {
            var commandContext = requireCommandContext(command);

            if (!isPlayerInGame(commandContext.gameID(), commandContext.authToken())) {
                sendError(ctx, "Cannot make move.");
                return null;
            }

            GameData gameData = getGameData(commandContext.gameID());
            ChessGame.TeamColor playerColor = getPlayerColor(gameData, commandContext.username());
            ChessGame.TeamColor otherPlayerColor = playerColor.equals(WHITE) ? BLACK : WHITE;
            String otherPlayerUsername = otherPlayerColor.equals(WHITE) ? gameData.whiteUsername() : gameData.blackUsername();
            ChessGame game = gameData.game();

            if (game.getTeamTurn() != playerColor) {
                sendError(ctx, "Not your turn.");
                return null;
            }

            if (!game.inProgress()) {
                sendError(ctx, "Game is over.");
                return null;
            }

            return new MoveContext(commandContext.gameID(), game, commandContext.username(), otherPlayerColor, otherPlayerUsername);
        } catch (BadRequestException e) {
            sendError(ctx, "Invalid game ID.");
            return null;
        } catch (NotAuthenticatedException e) {
            sendError(ctx, "Could not authenticate.");
            return null;
        } catch (DatabaseErrorException e) {
            sendError(ctx, "Server error. Try again later.");
            return null;
        }
    }

    private boolean applyMove(WsMessageContext ctx, MoveContext moveContext, ChessMove move) {
        try {
            moveContext.game().makeMove(move);
            gameDAO.updateGame(moveContext.gameID(), moveContext.game());
            return true;
        } catch (InvalidMoveException e) {
            sendError(ctx, "Invalid move.");
            return false;
        } catch (DataAccessException e) {
            sendError(ctx, "Server error.");
            return false;
        }
    }

    private void broadcastMoveResults(WsMessageContext ctx, ChessMove move, MoveContext moveContext,
                                      ConnectionManager connectionManager) {
        try {
            connectionManager.broadcastMessageToGame(moveContext.gameID(), null, new LoadGameMessage(moveContext.game()));
            var notification = new NotificationMessage(moveContext.username() +
                    " moved from " +
                    move.getStartPosition().toString() +
                    " to " +
                    move.getEndPosition().toString() + ".");
            connectionManager.broadcastMessageToGame(moveContext.gameID(), ctx.session, notification);

            if (moveContext.game().isInCheckmate(moveContext.otherPlayerColor())) {
                var checkmateNotification = new NotificationMessage("Checkmate!! " + moveContext.username() + " wins!!");
                connectionManager.broadcastMessageToGame(moveContext.gameID(), null, checkmateNotification);
                gameDAO.endGame(moveContext.gameID());
                return;
            }

            if (moveContext.game().isInStalemate(moveContext.otherPlayerColor())) {
                var stalemateNotification = new NotificationMessage("Stalemate!!");
                connectionManager.broadcastMessageToGame(moveContext.gameID(), null, stalemateNotification);
                gameDAO.endGame(moveContext.gameID());
                return;
            }

            if (moveContext.game().isInCheck(moveContext.otherPlayerColor())) {
                var checkNotification = new NotificationMessage(moveContext.otherPlayerUsername() + " is in check!");
                connectionManager.broadcastMessageToGame(moveContext.gameID(), null, checkNotification);
            }
        } catch (IOException | DataAccessException e) {
            sendError(ctx, "Server error.");
        }
    }

    private ChessGame.TeamColor getPlayerColor(GameData gameData, String username) {
        if (username.equals(gameData.whiteUsername())) {
            return WHITE;
        } else if (username.equals(gameData.blackUsername())) {
            return BLACK;
        }
        return null;
    }

    private void sendError(WsMessageContext ctx, String msg) {
        ctx.send(new ErrorMessage(msg).toString());
    }

    private record CommandContext(String authToken, Integer gameID, String username) {}

    private record MoveContext(Integer gameID, ChessGame game, String username,
                               ChessGame.TeamColor otherPlayerColor, String otherPlayerUsername) {}
}
