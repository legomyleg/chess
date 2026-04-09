package dataaccess;

import chess.ChessGame;
import chess.serialization.GsonFactory;
import com.google.gson.Gson;
import model.GameData;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SQLGameDAO implements GameDAO {

    public SQLGameDAO() {
        try {
            ConfigureDatabase.configureDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }



    @Override
    public Integer createGame(String gameName) throws DataAccessException {
        ChessGame game = new ChessGame();
        var statement = "INSERT INTO games(game_name, game) VALUES (?, ?)";
        Integer id = DBHelper.updateHelper(statement, gameName, GsonFactory.create().toJson(game));
        return id;
    }

    @Override
    public GameData getGameByGameName(String gameName) throws DataAccessException {
        Integer gameID = DBHelper.getIntHelper("games", "game_id", "game_name", gameName);
        if (gameID == null) {
            return null;
        }
        String gameJson = DBHelper.getStringHelper("games", "game", "game_name", gameName);
        ChessGame game = GsonFactory.create().fromJson(gameJson, ChessGame.class);
        String whiteUsername = DBHelper.getStringHelper("games", "white_username", "game_name", gameName);
        String blackUsername = DBHelper.getStringHelper("games", "black_username", "game_name", gameName);

        GameData gameData = new GameData(gameID, whiteUsername, blackUsername, gameName, game);

        return gameData;
    }

    @Override
    public GameData getGameByGameID(Integer gameID) throws DataAccessException {
        String gameName = DBHelper.getStringHelper("games", "game_name", "game_id", gameID);
        if (gameName == null) {
            return null;
        }
        String gameJson = DBHelper.getStringHelper("games", "game", "game_id", gameID);
        ChessGame game = GsonFactory.create().fromJson(gameJson, ChessGame.class);
        String whiteUsername = DBHelper.getStringHelper("games", "white_username", "game_id", gameID);
        String blackUsername = DBHelper.getStringHelper("games", "black_username", "game_id", gameID);

        GameData gameData = new GameData(gameID, whiteUsername, blackUsername, gameName, game);

        return gameData;
    }

    @Override
    public List<GameData> listAllGames() {
        try (Connection conn = DatabaseManager.getConnection()) {
            return listAllHelper(conn);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateWhitePlayer(Integer gameID, String whiteUsername) throws DataAccessException {
        var statement = "UPDATE games SET white_username='%s' WHERE game_id=%d".formatted(whiteUsername, gameID);
        DBHelper.updateHelper(statement);
    }

    @Override
    public void updateBlackPlayer(Integer gameID, String blackUsername) throws DataAccessException {
        var statement = "UPDATE games SET black_username='%s' WHERE game_id=%d".formatted(blackUsername, gameID);
        DBHelper.updateHelper(statement);
    }

    @Override
    public void updateGame(Integer gameID, ChessGame game) throws DataAccessException {
        String serializedGame = GsonFactory.create().toJson(game);
        var statement = "UPDATE games SET game='%s' WHERE game_id=%d".formatted(serializedGame, gameID);
        DBHelper.updateHelper(statement);
    }

    @Override
    public void endGame(Integer gameID) throws DataAccessException {
        ChessGame game = getGameByGameID(gameID).game();
        game.endGame();
        updateGame(gameID, game);
    }

    @Override
    public void deleteAll() {
        var statement = "TRUNCATE TABLE games";
        try {
            DBHelper.updateHelper(statement);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<GameData> listAllHelper(Connection conn) throws SQLException {
        List<GameData> games = new ArrayList<>();

        var statement = "SELECT * FROM games";
        var ps = conn.prepareStatement(statement);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            String gameName = rs.getString("game_name");
            String gameJson = rs.getString("game");
            ChessGame game = GsonFactory.create().fromJson(gameJson, ChessGame.class);
            Integer gameID = rs.getInt("game_id");
            String whiteUsername = rs.getString("white_username");
            String blackUsername = rs.getString("black_username");

            games.add(new GameData(gameID, whiteUsername, blackUsername, gameName, game));
        }

        return games;
    }
}
