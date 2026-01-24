package chess.PieceMoves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Collection;

public interface MoveCalculator {

    default boolean checkMove(ChessBoard board, ChessPosition startPosition, ChessPosition endPosition) {
        Collection<ChessMove> possibleMoves = getMoves(board, startPosition);
        return(possibleMoves.contains(new ChessMove(startPosition, endPosition)));
    }

    public Collection<ChessMove> getMoves (ChessBoard board, ChessPosition position);

}
