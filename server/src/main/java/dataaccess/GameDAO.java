package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;
import java.util.List;

public interface GameDAO {
    Integer createGame(String gameName) throws DataAccessException;

    GameData getGameByGameName(String gameName) throws DataAccessException;

    GameData getGameByGameID(Integer gameID) throws DataAccessException;

    List<GameData> listAllGames();

    void updateGame(Integer gameID, ChessGame game) throws DataAccessException;

    void updateWhitePlayer(Integer gameID, String whiteUsername) throws DataAccessException;

    void updateBlackPlayer(Integer gameID, String blackUsername) throws DataAccessException;

    void endGame(Integer gameID) throws DataAccessException;

    void deleteAll();
}
