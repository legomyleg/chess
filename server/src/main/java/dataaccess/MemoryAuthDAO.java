package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.Map;

import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {
    Map<String, AuthData> authDataByToken;

    public MemoryAuthDAO() {
        authDataByToken = new HashMap<>();
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    public AuthData createAuth(String username) throws DataAccessException {

        String authToken = generateToken();
        var authData = new AuthData(authToken, username);

        authDataByToken.put(authToken, authData);

        return authData;
    }

    @Override
    public AuthData getAuthByToken(String authToken) throws DataAccessException {
        return authDataByToken.get(authToken);
    }

    @Override
    public void deleteAuthByToken(String authToken) throws DataAccessException {
        var authData = authDataByToken.get(authToken);
        if (authData == null) {
            throw new DataAccessException("Cannot delete AuthData for unauthenticated user.");
        }
        authDataByToken.remove(authToken);
    }

    @Override
    public void deleteAll() {
        authDataByToken.clear();
    }
}
