package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import exception.DatabaseErrorException;
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
        if (authToken == null) {
            throw new NotAuthenticatedException();
        }

        try {
            if (authDAO.getAuthByToken(authToken) == null) {
                throw new NotAuthenticatedException();
            }
        } catch (DataAccessException e) {
            throw new DatabaseErrorException("could not verify auth");
        }

        try {
            return new GameListResult(gameDAO.listAllGames());
        } catch (RuntimeException e) {
            throw new DatabaseErrorException("could not list games");
        }
    }
}
