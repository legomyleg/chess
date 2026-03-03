package chess.piecemoves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KingMove implements MoveCalculator {

    @Override
    public Collection<ChessMove> getMoves(ChessBoard board, ChessPosition position) {
        int[] incR = {1, 1, 0, 1, 0, -1, -1, -1};
        int[] incC = {1, 0, 1, -1, -1, -1, 0, 1};
        return MoveCalculator.getSingleMoves(board, position, incR, incC);
    }
}
