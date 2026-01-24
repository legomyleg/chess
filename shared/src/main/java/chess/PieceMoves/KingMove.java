package chess.PieceMoves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KingMove implements MoveCalculator {

    @Override
    public boolean checkMove(ChessBoard board, ChessPosition startPosition, ChessPosition endPosition) {
        return false;
    }

    @Override
    public Collection<ChessMove> getMoves(ChessBoard board, ChessPosition position) {
        List<ChessMove> moves = new ArrayList<>();

        ChessPiece piece = board.getPiece(position);
        if (piece.getPieceType() != ChessPiece.PieceType.KING) {
            throw new RuntimeException("Piece " + piece + "is not king, KingMove cannot calculate move.");
        }

        int[] incR = {1, 1, 0, 1, 0, -1, -1, -1};
        int[] incC = {1, 0, 1, -1, -1, -1, 0, 1};
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
