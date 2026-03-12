package dataaccess;

import chess.ChessGame;
import chess.ChessPiece;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
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
        var statement = "TRUNCATE TABLE games";
        try {
            DBHelper.updateHelper(statement);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Gson createSerializer() {
        GsonBuilder gsonBuilder = new GsonBuilder();

        gsonBuilder.registerTypeAdapter(
                ChessPiece.class,
                (JsonDeserializer<ChessPiece>) (el, type, ctx) -> {
                    var obj = el.getAsJsonObject();

                    ChessGame.TeamColor color = ctx.deserialize(obj.get("pieceColor"), ChessGame.TeamColor.class);
                    ChessPiece.PieceType pieceType = ctx.deserialize(obj.get("type"), ChessPiece.PieceType.class);
                    boolean moved = obj.get("moved").getAsBoolean();

                    var piece = new ChessPiece(color, pieceType);
                    if (moved) {
                        piece.setMoved();
                    }
                    return piece;
                });

        return gsonBuilder.create();
    }
}
