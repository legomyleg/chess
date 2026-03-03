package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemoryGameDAO implements GameDAO {
    private Map<String, GameData> gameDataByGameName;
    private Map<Integer, GameData> gameDataByGameID;
    private int nextGameID;

    public MemoryGameDAO() {
        this.gameDataByGameName = new HashMap<>();
        this.gameDataByGameID = new HashMap<>();
        nextGameID = 1;
    }

    @Override
    public Integer createGame(String gameName) throws DataAccessException {
        if (gameDataByGameName.get(gameName) != null) {
            throw new DataAccessException("Game already exists.");
        }
        var gameData = new GameData(nextGameID++, null, null, gameName, new ChessGame());
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

    private void updateGameData(Integer gameID, GameData newGameData) throws DataAccessException {
        GameData existing = gameDataByGameID.get(gameID);
        if (existing == null) {
            throw new DataAccessException("Game not found.");
        }
        gameDataByGameID.replace(gameID, newGameData);
        gameDataByGameName.remove(existing.gameName());
        gameDataByGameName.put(newGameData.gameName(), newGameData);
    }

    @Override
    public void updateWhitePlayer(Integer gameID, String whiteUsername) throws DataAccessException {
        GameData gameData = getGameByGameID(gameID);
        updateGameData(gameID, new GameData(
                gameData.gameID(),
                whiteUsername,
                gameData.blackUsername(),
                gameData.gameName(),
                gameData.game()
        ));
    }

    @Override
    public void updateBlackPlayer(Integer gameID, String blackUsername) throws DataAccessException {
        GameData gameData = getGameByGameID(gameID);
        updateGameData(gameID, new GameData(
                gameData.gameID(),
                gameData.whiteUsername(),
                blackUsername,
                gameData.gameName(),
                gameData.game()
        ));
    }

    @Override
    public void deleteAll() {
        gameDataByGameName.clear();
        gameDataByGameID.clear();
    }
}
