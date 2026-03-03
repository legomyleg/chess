package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    Integer createGame(String gameName) throws DataAccessException;

    GameData getGameByGameName(String gameName) throws DataAccessException;

    GameData getGameByGameID(Integer gameID) throws DataAccessException;

    Collection<GameData> listAllGames();

    void updateWhitePlayer(Integer gameID, String whiteUsername) throws DataAccessException;

    void updateBlackPlayer(Integer gameID, String blackUsername) throws DataAccessException;

    void deleteAll();
}
