package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Collectors;

import static chess.serialization.ChessGameAdapter.createSerializer;
import static org.junit.jupiter.api.Assertions.*;

class SQLGameDAOTest extends SQLDAOTestHelper {
    private SQLGameDAO gameDAO;

    @Override
    protected void initializeDAO() {
        gameDAO = new SQLGameDAO();
    }

    @Test
    void createGamePositive() throws DataAccessException, SQLException {
        var gameName = "create-game";

        var gameID = gameDAO.createGame(gameName);

        assertNotNull(gameID);
        var storedGame = fetchStoredGame(gameID);
        assertNotNull(storedGame);
        assertEquals(gameName, storedGame.gameName());
        assertNull(storedGame.whiteUsername());
        assertNull(storedGame.blackUsername());
        assertEquals(new ChessGame(), storedGame.game());
    }

    @Test
    void createGameNegativeNullGameName() {
        assertThrows(RuntimeException.class, () -> gameDAO.createGame(null));
    }

    @Test
    void getGameByGameNamePositive() throws DataAccessException {
        var gameID = gameDAO.createGame("lookup-by-name");

        var storedGame = gameDAO.getGameByGameName("lookup-by-name");

        assertNotNull(storedGame);
        assertEquals(gameID, storedGame.gameID());
        assertEquals("lookup-by-name", storedGame.gameName());
        assertEquals(new ChessGame(), storedGame.game());
    }

    @Test
    void getGameByGameNameNegativeMissingGame() throws DataAccessException {
        assertNull(gameDAO.getGameByGameName("missing-game"));
    }

    @Test
    void getGameByGameIDPositive() throws DataAccessException {
        var gameID = gameDAO.createGame("lookup-by-id");

        var storedGame = gameDAO.getGameByGameID(gameID);

        assertNotNull(storedGame);
        assertEquals(gameID, storedGame.gameID());
        assertEquals("lookup-by-id", storedGame.gameName());
        assertEquals(new ChessGame(), storedGame.game());
    }

    @Test
    void getGameByGameIDNegativeMissingGame() throws DataAccessException {
        assertNull(gameDAO.getGameByGameID(-1));
    }

    @Test
    void listAllGamesPositive() throws DataAccessException {
        var firstID = gameDAO.createGame("list-game-first");
        var secondID = gameDAO.createGame("list-game-second");

        var games = gameDAO.listAllGames();
        var gameNames = games.stream().map(GameData::gameName).collect(Collectors.toSet());
        var gameIDs = games.stream().map(GameData::gameID).collect(Collectors.toSet());

        assertEquals(2, games.size());
        assertTrue(gameNames.contains("list-game-first"));
        assertTrue(gameNames.contains("list-game-second"));
        assertTrue(gameIDs.contains(firstID));
        assertTrue(gameIDs.contains(secondID));
    }

    @Test
    void listAllGamesNegativeEmptyDatabase() {
        assertTrue(gameDAO.listAllGames().isEmpty());
    }

    @Test
    void updateWhitePlayerPositive() throws DataAccessException {
        var gameID = gameDAO.createGame("update-white");
        var user = createAndInsertUser("white-player");

        gameDAO.updateWhitePlayer(gameID, user.username());

        var storedGame = gameDAO.getGameByGameID(gameID);
        assertNotNull(storedGame);
        assertEquals(user.username(), storedGame.whiteUsername());
    }

    @Test
    void updateWhitePlayerNegativeMissingUser() throws DataAccessException {
        var gameID = gameDAO.createGame("update-white-negative");

        assertThrows(DataAccessException.class, () -> gameDAO.updateWhitePlayer(gameID, "missing-user"));
    }

    @Test
    void updateBlackPlayerPositive() throws DataAccessException {
        var gameID = gameDAO.createGame("update-black");
        var user = createAndInsertUser("black-player");

        gameDAO.updateBlackPlayer(gameID, user.username());

        var storedGame = gameDAO.getGameByGameID(gameID);
        assertNotNull(storedGame);
        assertEquals(user.username(), storedGame.blackUsername());
    }

    @Test
    void updateBlackPlayerNegativeMissingUser() throws DataAccessException {
        var gameID = gameDAO.createGame("update-black-negative");

        assertThrows(DataAccessException.class, () -> gameDAO.updateBlackPlayer(gameID, "missing-user"));
    }

    @Test
    void deleteAllPositive() throws DataAccessException, SQLException {
        gameDAO.createGame("delete-all-game-first");
        gameDAO.createGame("delete-all-game-second");
        assertEquals(2, countGames());

        gameDAO.deleteAll();

        assertEquals(0, countGames());
    }

    private GameData fetchStoredGame(int gameID) throws SQLException {
        var statement = "SELECT game_id, white_username, black_username, game_name, game FROM games WHERE game_id = ?";
        try (var ps = cn.prepareStatement(statement)) {
            ps.setInt(1, gameID);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new GameData(
                        rs.getInt("game_id"),
                        rs.getString("white_username"),
                        rs.getString("black_username"),
                        rs.getString("game_name"),
                        createSerializer().fromJson(rs.getString("game"), ChessGame.class));
            }
        }
    }

    private int countGames() throws SQLException {
        try (var ps = cn.prepareStatement("SELECT COUNT(*) AS count FROM games");
             ResultSet rs = ps.executeQuery()) {
            assertTrue(rs.next());
            return rs.getInt("count");
        }
    }
}
