package dataaccess;

import java.sql.Connection;
import java.sql.SQLException;

public class ConfigureDatabase {

    static private final String[] CREATE_STATEMENTS = {
            """
            CREATE TABLE IF NOT EXISTS users(
            username VARCHAR(255) NOT NULL,
            password VARCHAR(255) NOT NULL,
            email VARCHAR(255),
            PRIMARY KEY (username)
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS auth(
            authToken VARCHAR(255) NOT NULL,
            username VARCHAR(255) NOT NULL,
            PRIMARY KEY (authToken),
            FOREIGN KEY (username) REFERENCES users(username)
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS games(
            game_id int AUTO_INCREMENT NOT NULL,
            white_username VARCHAR(255),
            black_username VARCHAR(255),
            game_name VARCHAR(255) NOT NULL,
            game TEXT,
            PRIMARY KEY (game_id),
            FOREIGN KEY (white_username) REFERENCES users(username),
            FOREIGN KEY (black_username) REFERENCES users(username)
            )
            """
    };

    public static void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : CREATE_STATEMENTS) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        }
        catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

}
