package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import exception.BadRequestException;
import exception.DatabaseErrorException;
import exception.NotAuthenticatedException;
import exception.ResponseException;
import model.AuthData;
import request.LoginRequest;
import result.LoginResult;

public class LoginService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public LoginService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public LoginResult login(LoginRequest request) throws ResponseException {

        String username = request.username();
        String password = request.password();

        if (username == null || password == null) {
            throw new BadRequestException();
        }

        try {
            if (!userDAO.verifyPassword(username, password)) {
                throw new NotAuthenticatedException();
            }
        } catch (DataAccessException e) {
            throw new DatabaseErrorException("could not verify user");
        }

        try {
            AuthData authData = authDAO.createAuth(username);
            return new LoginResult(username, authData.authToken());
        } catch (DataAccessException e) {
            throw new DatabaseErrorException("could not create session");
        }
    }
}
