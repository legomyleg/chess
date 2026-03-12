package dataaccess;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

class DBHelperTest {

    private Connection cn;

    private String[] beforeAllStatements = {
            "CREATE TABLE test_table(first_name varchar(255), last_name varchar(255))"
    };

    @BeforeAll
    static void beforeAll() {

        var statement = "CREATE TABLE test_table(first_name varchar(255), last_name varchar(255))";

        try (Connection conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new AssertionError("Prepare statement failed in set up.");
            }
        } catch (Exception e) {
            throw new AssertionError("Connection failed in set up process.");
        }
    }

    @AfterAll
    static void afterAll() {
        var statement = "DROP TABLE test_table";

        try (Connection conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Prepare statement failed in afterAll process.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Connection failed in afterAll process.");
        }
    }

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
    void updateHelperSuccess() {
        var statement = "INSERT INTO test_table(first_name, last_name) VALUES (?, ?)";
        var testFirstName = "Pearson";
        var testLastName = "Morris";
        assertDoesNotThrow(
                () -> DBHelper.updateHelper(statement, testFirstName, testLastName),
                "updateHelper failed to insert first and last name.");

        var queryStatement = "SELECT first_name, last_name FROM test_table WHERE first_name='Pearson'";
        try (var ps = cn.prepareStatement(queryStatement)) {
            ResultSet rs = ps.executeQuery();

            assertTrue(rs.next());

            assertEquals(rs.getString("first_name"), "Pearson");
            assertEquals(rs.getString("last_name"), "Morris");
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getStringHelper() {
    }
}