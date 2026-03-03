package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import exception.NotAuthenticatedException;
import exception.ResponseException;

public class LogoutService {
    private final AuthDAO authDAO;

    public LogoutService(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    public void logout(String authToken) throws ResponseException {
        try {
            authDAO.deleteAuthByToken(authToken);
        } catch (DataAccessException e) {
            throw new NotAuthenticatedException("Error: invalid auth token");
        }
    }

}
