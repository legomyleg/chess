package client;

import chess.ChessPiece;
import chess.ChessPosition;

public final class ChessInputParser {

    private ChessInputParser() {
    }

    public static ChessPosition parsePosition(String value) {
        if (value.length() != 2) {
            throw new IllegalArgumentException("Position must be in chess notation, like e2.");
        }

        char file = Character.toLowerCase(value.charAt(0));
        char rank = value.charAt(1);

        if (file < 'a' || file > 'h' || rank < '1' || rank > '8') {
            throw new IllegalArgumentException("Position must be in chess notation, like e2.");
        }

        return new ChessPosition(rank - '0', file - 'a' + 1);
    }

    public static ChessPiece.PieceType parsePromotionPiece(String value) {
        try {
            return ChessPiece.PieceType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Promotion piece must be QUEEN, ROOK, BISHOP, or KNIGHT.");
        }
    }
}
