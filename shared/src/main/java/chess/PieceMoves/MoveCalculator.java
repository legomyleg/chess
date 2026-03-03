package chess.piecemoves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;

public interface MoveCalculator {

    public Collection<ChessMove> getMoves (ChessBoard board, ChessPosition position);

}
