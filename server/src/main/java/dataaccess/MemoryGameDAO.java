package dataaccess;

import chess.ChessGame;
import model.GameData;

public class MemoryGameDAO implements GameDAO {
    @Override
    public void createGame(GameData gameData) throws DataAccessException {

    }

    @Override
    public GameData getGameByGameName(String gameName) throws DataAccessException {
        return null;
    }

    @Override
    public GameData getGameByGameID(String gameID) throws DataAccessException {
        return null;
    }

    @Override
    public void updateWhitePlayer(String gameID, String whiteUsername) throws DataAccessException {

    }

    @Override
    public void updateBlackPlayer(String gameID, String blackUsername) throws DataAccessException {

    }

    @Override
    public void updateGame(String gameID, ChessGame newGame) throws DataAccessException {

    }

    @Override
    public void updateGameName(String gameID, String gameName) throws DataAccessException {

    }

    @Override
    public void deleteGame(String gameID) throws DataAccessException {

    }
}
