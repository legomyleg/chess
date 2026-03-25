package chess.serialization;

import chess.ChessPiece;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class ChessPieceAdapter extends TypeAdapter<ChessPiece> {

    @Override
    public void write(JsonWriter out, ChessPiece piece) throws IOException {
        if (piece == null) {
            out.nullValue();
            return;
        }
        out.beginObject();
        out.name("pieceColor").value(piece.getTeamColor().name());
        out.name("type").value(piece.getPieceType().name());
        out.name("moved").value(piece.hasMoved());
        out.endObject();
    }

    @Override
    public ChessPiece read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
    }
}
