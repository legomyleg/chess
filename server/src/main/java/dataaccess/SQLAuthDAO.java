package dataaccess;

import model.AuthData;

public class SQLAuthDAO implements AuthDAO {
    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        String token = MemoryAuthDAO.generateToken();
        var statement = "INSERT INTO auth(authToken, username) VALUES (?, ?)";
        DBHelper.updateHelper(statement, token, username);

        return new AuthData(token, username);
    }

    @Override
    public AuthData getAuthByToken(String authToken) throws DataAccessException {
        var username = DBHelper.getStringHelper(
                "auth",
                "username",
                "authToken",
                authToken
        );

        return new AuthData(authToken, username);
    }

    @Override
    public void deleteAuthByToken(String authToken) throws DataAccessException {
        var statement = "DELETE FROM auth WHERE authToken=?";
        DBHelper.updateHelper(statement, authToken);
    }

    @Override
    public void deleteAll() {
        var statement = "TRUNCATE TABLE auth";

        try {
            DBHelper.updateHelper(statement);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
