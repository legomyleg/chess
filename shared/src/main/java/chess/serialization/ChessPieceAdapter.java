package chess.serialization;

import chess.ChessGame;
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


        ChessGame.TeamColor teamColor = null;
        ChessPiece.PieceType type = null;
        boolean moved = false;

        in.beginObject();
        while (in.hasNext()) {
            String name = in.nextName();
            switch (name) {
                case "pieceColor" -> teamColor = ChessGame.TeamColor.valueOf(in.nextString());
                case "type" -> type = ChessPiece.PieceType.valueOf(in.nextString());
                case "moved" -> moved = in.nextBoolean();
                default -> in.skipValue();
            }
        }
        in.endObject();

        ChessPiece piece = new ChessPiece(teamColor, type);
        if (moved) {
            piece.setMoved();
        }

        return piece;
    }
}
