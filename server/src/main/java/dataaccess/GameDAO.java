package dataaccess;

import chess.ChessGame;
import model.GameData;

public interface GameDAO {
    void createGame(GameData gameData) throws DataAccessException;

    GameData getGameByGameName(String gameName) throws DataAccessException;

    GameData getGameByGameID(String gameID) throws DataAccessException;

    void updateWhitePlayer(String gameID, String whiteUsername) throws DataAccessException;

    void updateBlackPlayer(String gameID, String blackUsername) throws DataAccessException;

    void updateGame(String gameID, ChessGame newGame) throws DataAccessException;

    void updateGameName(String gameID, String gameName) throws DataAccessException ;

    void deleteGame(String gameID) throws DataAccessException;
}
