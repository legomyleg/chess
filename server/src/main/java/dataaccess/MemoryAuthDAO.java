package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.Map;

import java.util.UUID;

public class MemoryAuthDAO implements AuthDOA {
    Map<String, AuthData> authDataByUsername;
    Map<String, AuthData> authDataByToken;

    public MemoryAuthDAO() {
        authDataByUsername = new HashMap<>();
        authDataByToken = new HashMap<>();
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        if (authDataByUsername.get(username) != null) {
            throw new AlreadyAuthenticatedException("User already authenticated.");
        }

        String authToken = generateToken();
        var authData = new AuthData(username, authToken);

        authDataByUsername.put(username, authData);
        authDataByToken.put(authToken, authData);

        return authData;
    }

    @Override
    public AuthData getAuthByToken(String authToken) throws DataAccessException {

        var authData = authDataByToken.get(authToken);
        if (authData == null) {
            throw new NotAuthenticatedException("Authentication token does not exist.");
        }

        return authData;
    }

    @Override
    public AuthData getAuthByUsername(String username) throws DataAccessException {
        var authData = authDataByUsername.get(username);
        if (authData == null) {
            throw new NotAuthenticatedException("User not authenticated.");
        }

        return authData;
    }

    @Override
    public void updateAuth(String newToken, String username) throws DataAccessException {
        if (authDataByUsername.get(username) == null) {
            throw new NotAuthenticatedException("User authentication does not already exist.");
        }
        authDataByUsername.replace(username, new AuthData(newToken, username));
    }

    @Override
    public void deleteAuthByToken(String authToken) throws DataAccessException {
        var authData = authDataByToken.get(authToken);
        if (authData == null) {
            throw new NotAuthenticatedException("Cannot delete AuthData for unauthenticated user.");
        }
        authDataByToken.remove(authToken);
        authDataByUsername.remove(authData.username());
    }

    @Override
    public void deleteAuthByUsername(String username) throws DataAccessException {
        var authData = authDataByUsername.get(username);
        if (authData == null) {
            throw new NotAuthenticatedException("Cannot delete AuthData for unauthenticated user.");
        }

        authDataByToken.remove(authData.authToken());
        authDataByUsername.remove(username);
    }
}
