package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.*;

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
        return gameDataByGameName.get(gameName);
    }

    @Override
    public GameData getGameByGameID(Integer gameID) throws DataAccessException {
        return gameDataByGameID.get(gameID);
    }

    @Override
    public List<GameData> listAllGames() {
        return new ArrayList<>(gameDataByGameName.values());
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
    public void updateGame(Integer gameID, ChessGame game) throws DataAccessException {
        var oldGameData = getGameByGameID(gameID);
        var newGameData = new GameData(gameID,
                oldGameData.whiteUsername(),
                oldGameData.blackUsername(),
                oldGameData.gameName(),
                game);

        gameDataByGameName.put(oldGameData.gameName(), newGameData);
        gameDataByGameID.put(gameID, newGameData);
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

    public void endGame(Integer gameID) throws DataAccessException {
        ChessGame game = getGameByGameID(gameID).game();
        game.endGame();
        updateGame(gameID, game);
    }

    @Override
    public void deleteAll() {
        gameDataByGameName.clear();
        gameDataByGameID.clear();
    }
}
