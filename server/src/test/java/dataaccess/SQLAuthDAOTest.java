package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class SQLAuthDAOTest {
    private Connection cn;
    private SQLAuthDAO authDAO;
    private SQLUserDAO userDAO;

    @BeforeEach
    void setUp() {
        try {
            authDAO = new SQLAuthDAO();
            userDAO = new SQLUserDAO();
            userDAO.deleteAll();
            cn = DatabaseManager.getConnection();
        } catch (DataAccessException e) {
            throw new RuntimeException("Connection failed on setUp.", e);
        }
    }

    @AfterEach
    void tearDown() {
        try {
            userDAO.deleteAll();
            cn.close();
        } catch (SQLException e) {
            throw new RuntimeException("Connection was either already closed or experienced some other error on tearDown.", e);
        }
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

    private UserData createAndInsertUser(String usernamePrefix) throws DataAccessException {
        var user = new UserData(usernamePrefix, "password123", usernamePrefix + "@gmail.com");
        userDAO.createUser(user);
        return user;
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
