package dataaccess;

import model.AuthData;

public interface AuthDAO {
    AuthData createAuth(String username) throws DataAccessException;

    AuthData getAuthByToken(String authToken) throws DataAccessException;

    AuthData getAuthByUsername(String username) throws DataAccessException;

    void updateAuth(String newToken, String username) throws DataAccessException;

    void deleteAuthByToken(String authToken) throws DataAccessException;

    void deleteAuthByUsername(String username) throws DataAccessException;

    void deleteAll();
}
