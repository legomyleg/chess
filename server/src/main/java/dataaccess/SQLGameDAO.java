package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.List;

public class SQLGameDAO implements GameDAO {
    @Override
    public Integer createGame(String gameName) throws DataAccessException {
        return 0;
    }

    @Override
    public GameData getGameByGameName(String gameName) throws DataAccessException {
        return null;
    }

    @Override
    public GameData getGameByGameID(Integer gameID) throws DataAccessException {
        return null;
    }

    @Override
    public Collection<GameData> listAllGames() {
        return List.of();
    }

    @Override
    public void updateWhitePlayer(Integer gameID, String whiteUsername) throws DataAccessException {

    }

    @Override
    public void updateBlackPlayer(Integer gameID, String blackUsername) throws DataAccessException {

    }

    @Override
    public void deleteAll() {

    }
}
