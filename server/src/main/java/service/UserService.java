package service;

import dataaccess.*;
import exception.AlreadyTakenException;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import request.RegisterRequest;
import result.RegisterResult;

import static exception.ResponseException.Code.ClientError;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDOA authDOA;

    public UserService(AuthDOA authDOA, UserDAO userDAO) {
        this.authDOA = authDOA;
        this.userDAO = userDAO;
    }


    public RegisterResult register(RegisterRequest registerRequest) throws ResponseException {

        var userData = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());

        AuthData authData = null;
        try {
            userDAO.createUser(userData);
            authData = authDOA.createAuth(registerRequest.username());
        } catch (DataAccessException e) {
            throw new AlreadyTakenException(ClientError, "Username already taken.");
        }

        return new RegisterResult(userData.username(), authData.authToken());
    }
}
