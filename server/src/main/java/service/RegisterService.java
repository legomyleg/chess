package service;

import dataaccess.*;
import exception.AlreadyTakenException;
import exception.BadRequestException;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import request.RegisterRequest;
import result.RegisterResult;

public class RegisterService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public RegisterService(AuthDAO authDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }


    public RegisterResult register(RegisterRequest request) throws ResponseException {

        String username = request.username();
        String password = request.password();
        String email = request.email();
        var userData = new UserData(username, password, email);

        if (username == null || password == null || email == null) {
            throw new BadRequestException("Error: Bad request");
        }

        AuthData authData = null;
        try {
            userDAO.createUser(userData);
            authData = authDAO.createAuth(username);
        } catch (DataAccessException e) {
            throw new AlreadyTakenException("Error: username already taken");
        }

        return new RegisterResult(username, authData.authToken());
    }
}
