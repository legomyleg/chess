package dataaccess;

import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.sql.Connection;
import java.sql.SQLException;

abstract class SQLDAOTestHelper {
    protected Connection cn;
    protected SQLUserDAO userDAO;

    @BeforeEach
    void setUpDatabase() {
        try {
            userDAO = new SQLUserDAO();
            initializeDAO();
            userDAO.deleteAll();
            cn = DatabaseManager.getConnection();
        } catch (DataAccessException e) {
            throw new RuntimeException("Connection failed on setUp.", e);
        }
    }

    @AfterEach
    void tearDownDatabase() {
        try {
            userDAO.deleteAll();
            cn.close();
        } catch (SQLException e) {
            throw new RuntimeException("Connection was either already closed or experienced some other error on tearDown.", e);
        }
    }

    protected void initializeDAO() {
    }

    protected UserData createAndInsertUser(String usernamePrefix) throws DataAccessException {
        var user = new UserData(usernamePrefix, "password123", usernamePrefix + "@gmail.com");
        userDAO.createUser(user);
        return user;
    }
}
