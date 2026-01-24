package chess.PieceMoves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Collection;

public interface MoveCalculator {

    public boolean checkMove(ChessBoard board, ChessPosition startPosition, ChessPosition endPosition);

    public Collection<ChessMove> getMoves (ChessBoard board, ChessPosition position);

}
