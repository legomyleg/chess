package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class SQLAuthDAOTest extends SQLDAOTestHelper {
    private SQLAuthDAO authDAO;

    @Override
    protected void initializeDAO() {
        authDAO = new SQLAuthDAO();
    }

    @Test
    void createAuthPositive() throws DataAccessException, SQLException {
        var user = createAndInsertUser("create-auth");

        var auth = authDAO.createAuth(user.username());

        assertNotNull(auth);
        assertNotNull(auth.authToken());
        assertFalse(auth.authToken().isBlank());
        assertEquals(user.username(), auth.username());
        assertEquals(auth, fetchStoredAuth(auth.authToken()));
    }

    @Test
    void createAuthNegativeMissingUser() {
        assertThrows(DataAccessException.class, () -> authDAO.createAuth("missing-user"));
    }

    @Test
    void getAuthByTokenPositive() throws DataAccessException {
        var auth = createAndInsertAuth("get-auth");

        var storedAuth = authDAO.getAuthByToken(auth.authToken());

        assertEquals(auth, storedAuth);
    }

    @Test
    void getAuthByTokenNegativeMissingToken() throws DataAccessException {
        assertNull(authDAO.getAuthByToken("missing-token"));
    }

    @Test
    void deleteAuthByTokenPositive() throws DataAccessException, SQLException {
        var auth = createAndInsertAuth("delete-auth");
        assertNotNull(fetchStoredAuth(auth.authToken()));

        authDAO.deleteAuthByToken(auth.authToken());

        assertNull(fetchStoredAuth(auth.authToken()));
        assertEquals(0, countAuths());
    }

    @Test
    void deleteAuthByTokenNegativeMissingToken() throws DataAccessException, SQLException {
        var auth = createAndInsertAuth("delete-auth-missing");

        authDAO.deleteAuthByToken("missing-token");

        assertNotNull(fetchStoredAuth(auth.authToken()));
        assertEquals(1, countAuths());
    }

    @Test
    void deleteAllPositive() throws DataAccessException, SQLException {
        createAndInsertAuth("delete-all-auth-first");
        createAndInsertAuth("delete-all-auth-second");
        assertEquals(2, countAuths());

        authDAO.deleteAll();

        assertEquals(0, countAuths());
    }

    private AuthData createAndInsertAuth(String usernamePrefix) throws DataAccessException {
        var user = createAndInsertUser(usernamePrefix);
        return authDAO.createAuth(user.username());
    }

    private AuthData fetchStoredAuth(String authToken) throws SQLException {
        var statement = "SELECT authToken, username FROM auth WHERE authToken = ?";
        try (var ps = cn.prepareStatement(statement)) {
            ps.setString(1, authToken);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new AuthData(rs.getString("authToken"), rs.getString("username"));
            }
        }
    }

    private int countAuths() throws SQLException {
        try (var ps = cn.prepareStatement("SELECT COUNT(*) AS count FROM auth");
             ResultSet rs = ps.executeQuery()) {
            assertTrue(rs.next());
            return rs.getInt("count");
        }
    }
}
