package chess.piecemoves;

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
        int[] rowDeltas = {1, 1, -1, -1};
        int[] colDeltas = {1, -1, -1, 1};
        return MoveCalculator.getLongMoves(board, position, rowDeltas, colDeltas);
    }
}
