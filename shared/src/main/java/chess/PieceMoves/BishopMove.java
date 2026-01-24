package chess.PieceMoves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BishopMove implements MoveCalculator {

    @Override
    public Collection<ChessMove> getMoves(ChessBoard board, ChessPosition position) {
        List<ChessMove> moves = new ArrayList<>();

        ChessPiece piece = board.getPiece(position);
        if (piece.getPieceType() != ChessPiece.PieceType.BISHOP) {
            throw new RuntimeException("Piece " + piece + "is not bishop, BishopMove cannot calculate move.");
        }

        for (int t = 0; t < 4; t++) {
            int[] incR = {1, 1, -1, -1};
            int[] incC = {1, -1, -1, 1};

            for (int i = 1; i <= 8; i++) {

                int rowIncrease = i * incR[t];
                int colIncrease = i * incC[t];
                ChessPosition newPosition = new ChessPosition(position.getRow() + rowIncrease, position.getColumn() + colIncrease);

                if (newPosition.outOfBounds()) {
                    break;
                }

                ChessPiece occupier = board.getPiece(newPosition);

                if (occupier != null) {
                    if (occupier.getTeamColor() != piece.getTeamColor()) {
                        moves.add(new ChessMove(position, newPosition));
                    }
                    break;
                }
                moves.add(new ChessMove(position, newPosition));
            }
        }

        return moves;
    }
}
