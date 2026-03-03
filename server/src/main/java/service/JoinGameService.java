package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import exception.AlreadyTakenException;
import exception.GameNotFoundException;
import exception.NotAuthenticatedException;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import request.JoinGameRequest;

public class JoinGameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public JoinGameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public void joinGame(JoinGameRequest request, String authToken) throws ResponseException {

        AuthData authData = null;
        try {
            authData = authDAO.getAuthByToken(authToken);
        } catch (DataAccessException e) {
            throw new NotAuthenticatedException("Error: user not authenticated");
        }

        String username = authData.username();
        ChessGame.TeamColor playerColor = request.playerColor();
        Integer gameID = request.gameID();

        GameData gameData = null;
        try {
            gameData = gameDAO.getGameByGameID(gameID);
        } catch (DataAccessException e) {
            throw new GameNotFoundException("Error: game not found");
        }

        String neededSpot = (playerColor == ChessGame.TeamColor.WHITE) ? gameData.whiteUsername() : gameData.blackUsername();
        if (neededSpot != null) {
            throw new AlreadyTakenException("Error: color not available");
        }

        try {
            if (playerColor == ChessGame.TeamColor.WHITE) {
                gameDAO.updateWhitePlayer(gameID, username);
            } else {
                gameDAO.updateBlackPlayer(gameID, username);
            }
        } catch (DataAccessException e) {
            throw new ResponseException(500, "Error: could not add player");
        }

    }
}
