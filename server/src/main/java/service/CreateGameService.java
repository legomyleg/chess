package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import exception.GameAlreadyExistsException;
import exception.NotAuthenticatedException;
import exception.ResponseException;
import request.CreateGameRequest;
import result.CreateGameResult;

public class CreateGameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public CreateGameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public CreateGameResult createGame(CreateGameRequest request, String authToken) throws ResponseException {

        try {
            authDAO.getAuthByToken(authToken);
        } catch (DataAccessException e) {
            throw new NotAuthenticatedException("Error: user not authenticated");
        }

        try {
            Integer gameID = gameDAO.createGame(request.gameName());
            return new CreateGameResult(gameID);
        } catch (DataAccessException e) {
            throw new GameAlreadyExistsException("Error: game already exists");
        }
    }
}
