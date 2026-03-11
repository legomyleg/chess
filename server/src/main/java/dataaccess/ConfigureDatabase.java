package dataaccess;

import java.sql.Connection;
import java.sql.SQLException;

public class ConfigureDatabase {

    static private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS users(
            username VARCHAR(255) NOT NULL,
            password VARCHAR(255) NOT NULL,
            email VARCHAR(255),
            PRIMARY KEY (username)
            );
            
            CREATE TABLE IF NOT EXISTS auth(
            authToken TEXT NOT NULL,
            username VARCHAR(255) NOT NULL,
            PRIMARY KEY (authToken),
            FOREIGN KEY (username) REFERENCES users(username)
            );
            
            CREATE TABLE IF NOT EXISTS games(
            gameID int AUTO_INCREMENT NOT NULL,
            whiteUsername VARCHAR(255),
            blackUsername VARCHAR(255),
            gameName VARCHAR(255) NOT NULL,
            game TEXT,
            PRIMARY KEY (gameID),
            FOREIGN KEY (whiteUsername) REFERENCES users(username),
            FOREIGN KEY (blackUsername) REFERENCES users(username)
            );
            """
    };

    public static void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
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
