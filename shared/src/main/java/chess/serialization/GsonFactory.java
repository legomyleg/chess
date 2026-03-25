package chess.serialization;

import chess.ChessPiece;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonFactory {
    public static Gson create() {
        return new GsonBuilder()
                .registerTypeAdapter(ChessPiece.class, new ChessPieceAdapter())
                .create();
    }
}
