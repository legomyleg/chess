package dataaccess;

import com.google.gson.Gson;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.SQLException;

public class SQLUserDAO implements UserDAO {

    public SQLUserDAO() throws DataAccessException {
        ConfigureDatabase.configureDatabase();
    }

    private void executeUpdate(String statement, String... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                for (int i = 0; i < params.length; i++) {
                    String param = params[i];
                    ps.setString(i + 1, param);
                }
                ps.executeUpdate();
            }
        }
        catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        var statement = "INSERT INTO users(username, password, email) VALUES (?, ?, ?)";
        var hashedPassword = hashPassword(user.password());
        DBHelper.updateHelper(statement, user.username(), hashedPassword, user.email());
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        var hashedPassword = DBHelper.getStringHelper(
                "users",
                "password",
                "username",
                username);

        var email = DBHelper.getStringHelper(
                "users",
                "email",
                "username",
                username
        );

        return new UserData(username, hashedPassword, email);
    }

    @Override
    public boolean verifyPassword(String username, String plainTextPassword) {
        try {
            var hashedPassword = DBHelper.getStringHelper(
                    "users",
                    "password",
                    "username",
                    username
            );

            return BCrypt.checkpw(plainTextPassword, hashedPassword);
        }
        catch (DataAccessException ex) {
            return false;
        }
    }

    @Override
    public void deleteAll() {

    }

    private String hashPassword(String plainTextPassword) {
        String hashedPassword = BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
        return hashedPassword;
    }

}
