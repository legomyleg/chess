package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import exception.AlreadyAuthenticatedException;
import exception.IncorrectPasswordException;
import exception.IncorrectUsernameException;
import exception.ResponseException;
import exception.ResponseException.Code;
import model.AuthData;
import model.UserData;
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

        UserData userData = null;
        try {
            userData = userDAO.getUser(username);
        } catch (DataAccessException e) {
            throw new IncorrectUsernameException(Code.ClientError, "Username incorrect.");
        }

        if (!userData.password().equals(password)) {
            throw new IncorrectPasswordException(Code.ClientError, "Incorrect password.");
        }

        AuthData authData = null;
        try {
            authData = authDAO.createAuth(username);
        } catch (DataAccessException e) {
            throw new AlreadyAuthenticatedException(Code.ClientError, "Already logged in.");
        }

        var result = new LoginResult(username, authData.authToken());

        return result;
    }
}
