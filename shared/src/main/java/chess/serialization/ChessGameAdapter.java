package chess.serialization;

import chess.ChessGame;
import chess.ChessPiece;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

public class ChessGameAdapter {
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
