package chess.PieceMoves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KnightMove implements MoveCalculator {
    @Override
    public Collection<ChessMove> getMoves(ChessBoard board, ChessPosition position) {
        List<ChessMove> moves = new ArrayList<>();

        ChessPiece piece = board.getPiece(position);
        if (piece.getPieceType() != ChessPiece.PieceType.KNIGHT) {
            throw new RuntimeException("Piece " + piece + "is not knight, KnightMove cannot calculate move.");
        }

        int[] incR = {2, 2, 1, -1, -2, -2, -1, 1};
        int[] incC = {1, -1, -2, -2, -1, 1, 2, 2};
        for (int i = 0; i < 8; i++) {
            ChessPosition newPosition = new ChessPosition(position.getRow() + incR[i], position.getColumn() + incC[i]);

            if (newPosition.outOfBounds()) {
                continue;
            }

            ChessPiece occupier = board.getPiece(newPosition);
            if (occupier != null) {
                if (occupier.getTeamColor() != piece.getTeamColor()) {
                    moves.add(new ChessMove(position, newPosition));
                }
                continue;
            }

            moves.add(new ChessMove(position, newPosition));
        }

        return moves;
    }
}
