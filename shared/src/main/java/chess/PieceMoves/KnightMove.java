package chess.piecemoves;

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
        int[] incR = {2, 2, 1, -1, -2, -2, -1, 1};
        int[] incC = {1, -1, -2, -2, -1, 1, 2, 2};
        return MoveCalculator.getSingleMoves(board, position, incR, incC);
    }
}
