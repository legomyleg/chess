package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import exception.AlreadyTakenException;
import exception.BadRequestException;
import exception.DatabaseErrorException;
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

        if (request.playerColor() == null) {
            throw new BadRequestException();
        }
        if (authToken == null) {
            throw new NotAuthenticatedException();
        }

        try {
            AuthData authData = authDAO.getAuthByToken(authToken);
            if (authData == null) {
                throw new NotAuthenticatedException();
            }

            String username = authData.username();
            ChessGame.TeamColor playerColor = request.playerColor();
            Integer gameID = request.gameID();

            GameData gameData = gameDAO.getGameByGameID(gameID);
            if (gameData == null) {
                throw new BadRequestException();
            }

            String neededSpot = (playerColor == ChessGame.TeamColor.WHITE) ? gameData.whiteUsername() : gameData.blackUsername();
            if (neededSpot != null) {
                throw new AlreadyTakenException();
            }

            if (playerColor == ChessGame.TeamColor.WHITE) {
                gameDAO.updateWhitePlayer(gameID, username);
            } else {
                gameDAO.updateBlackPlayer(gameID, username);
            }
        } catch (DataAccessException e) {
            throw new DatabaseErrorException("could not join game");
        }
    }
}
