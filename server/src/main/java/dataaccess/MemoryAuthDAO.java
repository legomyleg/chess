package dataaccess;

import model.AuthData;

public class MemoryAuthDAO implements AuthDOA {
    AuthData authData;

    @Override
    public AuthData createAuth(String username) throws DataAccessException {

    }

    @Override
    public AuthData getAuthByToken(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public AuthData getAuthByUsername(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void updateAuth(String newToken, String username) throws DataAccessException {

    }

    @Override
    public void deleteAuthByToken(String authToken) throws DataAccessException {

    }

    @Override
    public void deleteAuthByUsername(String username) throws DataAccessException {

    }
}
