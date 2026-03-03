package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;

public interface GameDAO {
    Integer createGame(String gameName) throws DataAccessException;

    GameData getGameByGameName(String gameName) throws DataAccessException;

    GameData getGameByGameID(Integer gameID) throws DataAccessException;

    Collection<GameData> listAllGames();

    void updateWhitePlayer(Integer gameID, String whiteUsername) throws DataAccessException;

    void updateBlackPlayer(Integer gameID, String blackUsername) throws DataAccessException;

    void updateGame(Integer gameID, ChessGame newGame) throws DataAccessException;

    void updateGameName(Integer gameID, String gameName) throws DataAccessException ;

    void deleteGame(Integer gameID) throws DataAccessException;

    void deleteAll();
}
