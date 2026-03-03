package service;

import dataaccess.*;
import exception.AlreadyTakenException;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import request.RegisterRequest;
import result.RegisterResult;

import static exception.ResponseException.Code.ClientError;

public class RegisterService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public RegisterService(AuthDAO authDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }


    public RegisterResult register(RegisterRequest request) throws ResponseException {

        var userData = new UserData(request.username(), request.password(), request.email());

        AuthData authData = null;
        try {
            userDAO.createUser(userData);
            authData = authDAO.createAuth(request.username());
        } catch (DataAccessException e) {
            throw new AlreadyTakenException(ClientError, "Username already taken.");
        }

        return new RegisterResult(userData.username(), authData.authToken());
    }
}
