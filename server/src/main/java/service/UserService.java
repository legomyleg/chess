package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import request.RegisterRequest;
import result.RegisterResult;

public class UserService {
    UserDAO userDAO;
    AuthDOA authDOA;

    public UserService(AuthDOA authDOA, UserDAO userDAO) {
        this.authDOA = authDOA;
        this.userDAO = userDAO;
    }


    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {

        UserData user = userDAO.getUser(registerRequest.username());

        if (user != null) {
            throw new AlreadyTakenException("Username already taken.");
        }

        var userData = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
        userDAO.createUser(userData);

        AuthData authData = authDOA.createAuth(userData.username());

        return new RegisterResult(userData.username(), authData.authToken());
    }
}
