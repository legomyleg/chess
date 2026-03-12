package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import exception.BadRequestException;
import exception.DatabaseErrorException;
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

        if (request.gameName() == null) {
            throw new BadRequestException();
        }
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
            Integer gameID = gameDAO.createGame(request.gameName());
            return new CreateGameResult(gameID);
        } catch (DataAccessException e) {
            throw new DatabaseErrorException("could not create game");
        }
    }
}
