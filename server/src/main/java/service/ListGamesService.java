package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import exception.NotAuthenticatedException;
import exception.ResponseException;
import result.GameListResult;

public class ListGamesService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public ListGamesService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public GameListResult listGames(String authToken) throws ResponseException {

        try {
            authDAO.getAuthByToken(authToken);
        } catch (DataAccessException e) {
            throw new NotAuthenticatedException(ResponseException.Code.ClientError, "User not authenticated");
        }

        return new GameListResult(gameDAO.listAllGames());
    }
}
