package dataaccess;

import model.AuthData;

public class AuthDOA {
    public void createAuth(AuthData authData) throws DataAccessException {
        throw new RuntimeException("Not implemented");
    }

    public AuthData getAuthByToken(String authToken) throws DataAccessException {
        throw new RuntimeException("Not implemented");
    }

    public AuthData getAuthByUsername(String username) throws DataAccessException {
        throw new RuntimeException("Not implemented");
    }

    public void updateAuth(String newToken, String username) throws DataAccessException {
        throw new RuntimeException("Not implemented");
    }

    public void deleteAuthByToken(String authToken) throws DataAccessException {
        throw new RuntimeException("Not implemented");
    }

    public void deleteAuthByUsername(String username) throws DataAccessException {
        throw new RuntimeException("Not implemented");
    }
}
