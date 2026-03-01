package dataaccess;

import chess.ChessGame;
import model.GameData;

public class GameDAO {
    public void createGame(GameData gameData) {
        throw new RuntimeException("Not implemented");
    }

    public GameData getGameByGameName(String gameName) {
        throw new RuntimeException("Not implemented");
    }

    public GameData getGameByGameID(String gameID) {
        throw new RuntimeException("Not implemented");
    }

    public void updateWhitePlayer(String gameID, String whiteUsername) {
        throw new RuntimeException("Not implemented");
    }

    public void updateBlackPlayer(String gameID, String blackUsername) {
        throw new RuntimeException("Not implemented");
    }

    public void updateGame(String gameID, ChessGame newGame) {
        throw new RuntimeException("Not implemented");
    }

    public void updateGameName(String gameID, String gameName) {
        throw new RuntimeException("Not implemented");
    }

    public void deleteGame(String gameID) {
        throw new RuntimeException("Not implemented");
    }
}
