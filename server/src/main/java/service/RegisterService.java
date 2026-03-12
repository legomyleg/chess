package service;

import dataaccess.*;
import exception.AlreadyTakenException;
import exception.BadRequestException;
import exception.DatabaseErrorException;
import exception.ResponseException;
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

        if (username == null || password == null || email == null) {
            throw new BadRequestException();
        }

        var userData = new UserData(username, password, email);

        try {
            if (userDAO.getUser(username) != null) {
                throw new AlreadyTakenException();
            }
        } catch (DataAccessException e) {
            throw new DatabaseErrorException("could not access users");
        }

        try {
            userDAO.createUser(userData);
            var authData = authDAO.createAuth(username);
            return new RegisterResult(username, authData.authToken());
        } catch (DataAccessException e) {
            throw new DatabaseErrorException("could not create user");
        }
    }
}
