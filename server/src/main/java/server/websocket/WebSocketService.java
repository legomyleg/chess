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

        String authToken = command.getAuthToken();
        Integer gameID = command.getGameID();

        validateAuthToken(authToken);
        validateGameExists(gameID);
        String username = getUsername(authToken);
        boolean observing = !isPlayerInGame(gameID, authToken);

        connectionManager.connections.putIfAbsent(gameID, new ArrayList<>());
        connectionManager.addSessionToGame(gameID, ctx.session);

        ctx.send(new LoadGameMessage(getGameData(gameID).game()).toString());

        String role = observing ? "an observer" : "a player";
        var notification = new NotificationMessage(username + " joined the game as " + role + ".");
        try {
            connectionManager.broadcastMessageToGame(gameID, ctx.session, notification);
        } catch (IOException e) {
            throw new ResponseException(500, "Server suffered an error.");
        }
    }

    public void leave(WsMessageContext ctx, UserGameCommand command, ConnectionManager connectionManager) throws ResponseException {
        assert command.getCommandType().equals(UserGameCommand.CommandType.LEAVE);

        String authToken = command.getAuthToken();
        Integer gameID = command.getGameID();

        validateAuthToken(authToken);
        validateGameExists(gameID);
        String username = getUsername(authToken);

        connectionManager.removeSessionFromGame(gameID, ctx.session);

        var notification = new NotificationMessage(username + " has left the game.");

        try {
            connectionManager.broadcastMessageToGame(gameID, null, notification);
        } catch (IOException e) {
            throw new ResponseException(500, "Error: Server could not broadcast notification.");
        }
    }

    public void resign(WsMessageContext ctx, UserGameCommand command, ConnectionManager connectionManager) throws ResponseException {
        assert command.getCommandType().equals(UserGameCommand.CommandType.RESIGN);

        String authToken = command.getAuthToken();
        Integer gameID = command.getGameID();

        validateAuthToken(authToken);
        validateGameExists(gameID);
        String username = getUsername(authToken);

        if (!isPlayerInGame(gameID, authToken)) {
            throw new BadRequestException();
        }
    }


    public void makeMove(WsMessageContext ctx, MakeMoveCommand command, ConnectionManager connectionManager) {
        String authToken = command.getAuthToken();
        Integer gameID = command.getGameID();
        ChessMove move = command.getMove();
        ChessGame game;
        String username;
        ChessGame.TeamColor otherPlayerColor;
        String otherPlayerUsername;

        try {
            validateGameExists(gameID);
            validateAuthToken(authToken);

            if (!isPlayerInGame(gameID, authToken)) {
                sendError(ctx,"Cannot make move.");
                return;
            }
            GameData gameData = getGameData(gameID);
            game = gameData.game();
            username = getUsername(authToken);
            var playerColor = username.equals(gameData.whiteUsername()) ? WHITE : BLACK;
            otherPlayerColor = playerColor.equals(WHITE) ? BLACK : WHITE;
            otherPlayerUsername = otherPlayerColor.equals(WHITE) ? gameData.whiteUsername() : gameData.blackUsername();

            if (game.getTeamTurn() != playerColor) {
                sendError(ctx,"Not your turn.");
                return;
            }

        } catch (BadRequestException e) {
            sendError(ctx, "Invalid game ID.");
            return;
        } catch (NotAuthenticatedException e) {
            sendError(ctx, "Could not authenticate.");
            return;
        } catch (DatabaseErrorException e) {
            sendError(ctx, "Server error. Try again later.");
            return;
        }

        try {
            game.makeMove(move);
            gameDAO.updateGame(gameID, game);
        } catch (InvalidMoveException e) {
            sendError(ctx, "Invalid move.");
            return;
        } catch (DataAccessException e) {
            sendError(ctx, "Server error.");
            return;
        }

        try {
            connectionManager.broadcastMessageToGame(gameID, null, new LoadGameMessage(game));
            var notification = new NotificationMessage(username +
                    " moved from " +
                    move.getStartPosition().toString() +
                    " to " +
                    move.getEndPosition().toString() + ".");
            connectionManager.broadcastMessageToGame(gameID, ctx.session, notification);

            if (game.isInCheckmate(otherPlayerColor)) {
                var checkmateNotification = new NotificationMessage("Checkmate!!");
                var winNotification = new NotificationMessage(username + " wins!!!");
                connectionManager.broadcastMessageToGame(gameID, null, checkmateNotification);
                connectionManager.broadcastMessageToGame(gameID, null, winNotification);
                gameDAO.endGame(gameID);
                return;
            }

            if (game.isInStalemate(otherPlayerColor)) {
                var stalemateNotification = new NotificationMessage("Stalemate!!");
                connectionManager.broadcastMessageToGame(gameID, null, stalemateNotification);
                gameDAO.endGame(gameID);
                return;
            }

            if (game.isInCheck(otherPlayerColor)) {
                var checkNotification = new NotificationMessage(otherPlayerUsername + " is in check!");
                connectionManager.broadcastMessageToGame(gameID, null, checkNotification);
            }
        } catch (IOException | DataAccessException e) {
            sendError(ctx, "Server error.");
        }
    }

    private void sendError(WsMessageContext ctx, String msg) {
        ctx.send(new ErrorMessage(msg).toString());
    }
}

