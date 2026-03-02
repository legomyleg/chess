package dataaccess;

import model.AuthData;

public interface AuthDOA {
    void createAuth(AuthData authData) throws DataAccessException;

    AuthData getAuthByToken(String authToken) throws DataAccessException;

    AuthData getAuthByUsername(String username) throws DataAccessException;

    void updateAuth(String newToken, String username) throws DataAccessException;

    void deleteAuthByToken(String authToken) throws DataAccessException;

    void deleteAuthByUsername(String username) throws DataAccessException;
}
