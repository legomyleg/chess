package chess.piecemoves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface MoveCalculator {

    public Collection<ChessMove> getMoves (ChessBoard board, ChessPosition position);

    static Collection<ChessMove> getLongMoves(ChessBoard board, ChessPosition position, int[] rowDeltas, int[] colDeltas) {
        List<ChessMove> moves = new ArrayList<>();
        ChessPiece piece = board.getPiece(position);

        for (int t = 0; t < rowDeltas.length; t++) {
            for (int i = 1; i <= 8; i++) {
                int rowIncrease = i * rowDeltas[t];
                int colIncrease = i * colDeltas[t];
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

    static Collection<ChessMove> getSingleMoves(ChessBoard board, ChessPosition position, int[] rowDeltas, int[] colDeltas) {
        List<ChessMove> moves = new ArrayList<>();
        ChessPiece piece = board.getPiece(position);

        for (int i = 0; i < rowDeltas.length; i++) {
            ChessPosition newPosition = new ChessPosition(position.getRow() + rowDeltas[i], position.getColumn() + colDeltas[i]);

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
