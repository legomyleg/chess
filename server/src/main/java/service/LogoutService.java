package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import exception.DatabaseErrorException;
import exception.NotAuthenticatedException;
import exception.ResponseException;

public class LogoutService {
    private final AuthDAO authDAO;

    public LogoutService(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    public void logout(String authToken) throws ResponseException {
        if (authToken == null) {
            throw new NotAuthenticatedException();
        }
        try {
            if (authDAO.getAuthByToken(authToken) == null) {
                throw new NotAuthenticatedException();
            }
            authDAO.deleteAuthByToken(authToken);
        } catch (DataAccessException e) {
            throw new DatabaseErrorException("could not delete auth");
        }
    }

}
