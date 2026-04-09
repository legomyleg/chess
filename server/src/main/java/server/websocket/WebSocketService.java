package server.websocket;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import exception.BadRequestException;
import exception.DatabaseErrorException;
import exception.NotAuthenticatedException;
import model.GameData;

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
            if (gameData.whiteUsername().equals(username) || gameData.blackUsername().equals(username)) {
                return true;
            }
            return false;
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


}
