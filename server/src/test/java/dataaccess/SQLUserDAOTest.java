package dataaccess;

import model.UserData;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

class SQLUserDAOTest {
    Connection cn;

    @BeforeEach
    void setUp() {
        try {
            cn = DatabaseManager.getConnection();
        }
        catch (DataAccessException e) {
            throw new RuntimeException("Connection failed on setUp.");
        }
    }

    @AfterEach
    void tearDown() {
        try {
            cn.close();
        }
        catch (SQLException e) {
            throw new RuntimeException("Connection was either already closed or experienced some other error on tearDown.");
        }
    }

    @Test
    void createUserSuccess() throws DataAccessException {
        String username = "pboi1";
        String password = "password";
        String email = "pboi@gmail.com";
        var user = new UserData(username, password, email);
        var dao = new SQLUserDAO();

        dao.createUser(user);

        var statement = "SELECT username, email FROM users WHERE username=?";
        try (var ps = cn.prepareStatement(statement)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    assertEquals(rs.getString("username"), username);
                    assertEquals(rs.getString("email"), email);
                }
            }
        }

        catch (SQLException ex) {
            throw new AssertionError("Database access should not throw SQLException.");
        }
    }
}