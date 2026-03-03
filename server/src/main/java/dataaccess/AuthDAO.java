package dataaccess;

import model.AuthData;

public interface AuthDAO {
    AuthData createAuth(String username) throws DataAccessException;

    AuthData getAuthByToken(String authToken) throws DataAccessException;

    void deleteAuthByToken(String authToken) throws DataAccessException;

    void deleteAll();
}
