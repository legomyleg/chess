package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemoryGameDAO implements GameDAO {
    private Map<String, GameData> gameDataByGameName;
    private Map<Integer, GameData> gameDataByGameID;
    private Integer newGameID;

    public MemoryGameDAO() {
        this.gameDataByGameName = new HashMap<>();
        this.gameDataByGameID = new HashMap<>();
        newGameID = Integer.valueOf(1);
    }

    @Override
    public Integer createGame(String gameName) throws DataAccessException {
        if (gameDataByGameName.get(gameName) != null) {
            throw new DataAccessException("Game already exists.");
        }
        var gameData = new GameData(newGameID++, null, null, gameName, new ChessGame());
        gameDataByGameName.put(gameName, gameData);
        gameDataByGameID.put(gameData.gameID(), gameData);

        return gameData.gameID();
    }

    @Override
    public GameData getGameByGameName(String gameName) throws DataAccessException {
        var gameData = gameDataByGameName.get(gameName);
        if (gameData == null) {
            throw new DataAccessException("Game not found.");
        }

        return gameData;
    }

    @Override
    public GameData getGameByGameID(Integer gameID) throws DataAccessException {
        var gameData = gameDataByGameID.get(gameID);
        if (gameData == null) {
            throw new DataAccessException("Game not found.");
        }

        return gameData;
    }

    @Override
    public Collection<GameData> listAllGames() {
        return gameDataByGameName.values();
    }

    @Override
    public void updateWhitePlayer(Integer gameID, String whiteUsername) throws DataAccessException {
        var gameData = gameDataByGameID.get(gameID);
        if (gameData == null) {
            throw new DataAccessException("Game not found.");
        }
        var newGameData = new GameData(
                gameData.gameID(),
                whiteUsername,
                gameData.blackUsername(),
                gameData.gameName(),
                gameData.game()
        );

        gameDataByGameID.replace(gameID, newGameData);
    }

    @Override
    public void updateBlackPlayer(Integer gameID, String blackUsername) throws DataAccessException {
        var gameData = gameDataByGameID.get(gameID);
        if (gameData == null) {
            throw new DataAccessException("Game not found.");
        }
        var newGameData = new GameData(
                gameData.gameID(),
                gameData.whiteUsername(),
                blackUsername,
                gameData.gameName(),
                gameData.game()
        );

        gameDataByGameID.replace(gameID, newGameData);
    }

    @Override
    public void updateGame(Integer gameID, ChessGame newGame) throws DataAccessException {
        var gameData = gameDataByGameID.get(gameID);
        if (gameData == null) {
            throw new DataAccessException("Game not found.");
        }
        var newGameData = new GameData(
                gameData.gameID(),
                gameData.whiteUsername(),
                gameData.blackUsername(),
                gameData.gameName(),
                newGame
        );

        gameDataByGameID.replace(gameID, newGameData);
    }

    @Override
    public void updateGameName(Integer gameID, String gameName) throws DataAccessException {
        var gameData = gameDataByGameID.get(gameID);
        if (gameData == null) {
            throw new DataAccessException("Game not found.");
        }
        var newGameData = new GameData(
                gameData.gameID(),
                gameData.whiteUsername(),
                gameData.blackUsername(),
                gameName,
                gameData.game()
        );

        gameDataByGameID.replace(gameID, newGameData);
    }

    @Override
    public void deleteGame(Integer gameID) throws DataAccessException {
        var gameData = gameDataByGameID.get(gameID);
        if (gameData == null) {
            throw new DataAccessException("Game not found.");
        }
        String gameName = gameData.gameName();

        gameDataByGameID.remove(gameID);
        gameDataByGameName.remove(gameName);

    }

    @Override
    public void deleteAll() {
        gameDataByGameName.clear();
        gameDataByGameID.clear();
    }
}
