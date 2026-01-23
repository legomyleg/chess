package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Map;
import static java.util.Map.entry;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final PieceType type;
    private final ChessGame.TeamColor pieceColor;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.type = type;
        this.pieceColor = pieceColor;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }


    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return new ArrayList<ChessMove>();
    }

    @Override
    public String toString() {
        Map<PieceType, String> whitePieces = Map.ofEntries(
                entry(PieceType.ROOK, "R"),
                entry(PieceType.KNIGHT, "N"),
                entry(PieceType.BISHOP, "B"),
                entry(PieceType.KING, "K"),
                entry(PieceType.QUEEN, "Q"),
                entry(PieceType.PAWN, "P")
        );
        Map<PieceType, String> blackPieces = Map.ofEntries(
                entry(PieceType.ROOK, "r"),
                entry(PieceType.KNIGHT, "n"),
                entry(PieceType.BISHOP, "b"),
                entry(PieceType.KING, "k"),
                entry(PieceType.QUEEN, "q"),
                entry(PieceType.PAWN, "p")
        );


        if (pieceColor == ChessGame.TeamColor.WHITE) {
            return whitePieces.get(type);
        }
        return blackPieces.get(type);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return type == that.type && pieceColor == that.pieceColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, pieceColor);
    }
}
