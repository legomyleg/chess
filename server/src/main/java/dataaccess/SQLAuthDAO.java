package dataaccess;

import model.AuthData;

public class SQLAuthDAO implements AuthDAO {
    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        return null;
    }

    @Override
    public AuthData getAuthByToken(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuthByToken(String authToken) throws DataAccessException {

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
