package dataaccess;

import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class SQLUserDAOTest {
    private Connection cn;
    private SQLUserDAO dao;

    @BeforeEach
    void setUp() {
        try {
            dao = new SQLUserDAO();
            dao.deleteAll();
            cn = DatabaseManager.getConnection();
        } catch (DataAccessException e) {
            throw new RuntimeException("Connection failed on setUp.", e);
        }
    }

    @AfterEach
    void tearDown() {
        try {
            dao.deleteAll();
            cn.close();
        } catch (SQLException e) {
            throw new RuntimeException("Connection was either already closed or experienced some other error on tearDown.", e);
        }
    }

    @Test
    void createUserPositive() throws DataAccessException, SQLException {
        var user = createUserData("create-user");

        dao.createUser(user);

        var storedUser = fetchStoredUser(user.username());
        assertNotNull(storedUser);
        assertEquals(user.username(), storedUser.username());
        assertEquals(user.email(), storedUser.email());
        assertNotEquals(user.password(), storedUser.password());
    }

    @Test
    void createUserNegativeDuplicateUsername() throws DataAccessException {
        var user = createUserData("duplicate-user");
        dao.createUser(user);

        assertThrows(DataAccessException.class, () -> dao.createUser(user));
    }

    @Test
    void getUserPositive() throws DataAccessException {
        var user = createAndInsertUser("get-user");

        var storedUser = dao.getUser(user.username());

        assertNotNull(storedUser);
        assertEquals(user.username(), storedUser.username());
        assertEquals(user.email(), storedUser.email());
        assertNotEquals(user.password(), storedUser.password());
    }

    @Test
    void getUserNegativeMissingUser() throws DataAccessException {
        assertNull(dao.getUser("missing-user"));
    }

    @Test
    void verifyPasswordPositive() throws DataAccessException {
        var user = createAndInsertUser("verify-password");

        assertTrue(dao.verifyPassword(user.username(), user.password()));
    }

    @Test
    void verifyPasswordNegativeWrongPassword() throws DataAccessException {
        var user = createAndInsertUser("verify-password-negative");

        assertFalse(dao.verifyPassword(user.username(), "wrong-password"));
    }

    @Test
    void deleteAllPositive() throws DataAccessException, SQLException {
        var firstUser = createAndInsertUser("delete-all-first");
        var secondUser = createAndInsertUser("delete-all-second");
        assertNotNull(fetchStoredUser(firstUser.username()));
        assertNotNull(fetchStoredUser(secondUser.username()));

        dao.deleteAll();

        assertEquals(0, countUsers());
    }

    private UserData createAndInsertUser(String usernamePrefix) throws DataAccessException {
        var user = createUserData(usernamePrefix);
        dao.createUser(user);
        return user;
    }

    private UserData createUserData(String usernamePrefix) {
        return new UserData(usernamePrefix, "password123", usernamePrefix + "@gmail.com");
    }

    private UserData fetchStoredUser(String username) throws SQLException {
        var statement = "SELECT username, password, email FROM users WHERE username = ?";
        try (var ps = cn.prepareStatement(statement)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new UserData(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"));
            }
        }
    }

    private int countUsers() throws SQLException {
        try (var ps = cn.prepareStatement("SELECT COUNT(*) AS count FROM users");
             ResultSet rs = ps.executeQuery()) {
            assertTrue(rs.next());
            return rs.getInt("count");
        }
    }
}
