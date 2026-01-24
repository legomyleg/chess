package chess.PieceMoves;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PawnMove implements MoveCalculator {

    @Override
    public Collection<ChessMove> getMoves(ChessBoard board, ChessPosition position) {
        List<ChessMove> moves = new ArrayList<>();

        ChessPiece piece = board.getPiece(position);
        if (piece.getPieceType() != ChessPiece.PieceType.PAWN) {
            throw new RuntimeException("Piece " + piece + "is not pawn, PawnMove cannot calculate move.");
        }

        int direction = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 1 : -1;
        int startRow = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 2 : 7;

        ChessPosition forwardOne = new ChessPosition(position.getRow() + direction, position.getColumn());
        ChessPosition forwardTwo = new ChessPosition(position.getRow() + (2 * direction), position.getColumn());
        ChessPosition right = new ChessPosition(position.getRow() + direction, position.getColumn() + 1);
        ChessPosition left = new ChessPosition(position.getRow() + direction, position.getColumn() - 1);

        if (addIfEmpty(board, moves, position, forwardOne) && (position.getRow() == startRow)) {
            addIfEmpty(board, moves, position, forwardTwo);
        }
        addIfEnemy(board, moves, position, right);
        addIfEnemy(board, moves, position, left);

        return moves;
    }

    private boolean addIfEmpty(ChessBoard board, List<ChessMove> moves, ChessPosition startPosition, ChessPosition endPosition) {
        if (board.checkClear(endPosition)) {
            addAndCheckPromotion(board, moves, startPosition, endPosition);
            return true;
        }
        return false;
    }
    private boolean addIfEnemy(ChessBoard board, List<ChessMove> moves, ChessPosition startPosition, ChessPosition endPosition) {
        if (endPosition.outOfBounds() || board.checkClear(endPosition)) {
            return false;
        }

        ChessGame.TeamColor startColor = board.getPiece(startPosition).getTeamColor();
        ChessGame.TeamColor endColor = board.getPiece(endPosition).getTeamColor();

        if (startColor != endColor) {
            addAndCheckPromotion(board, moves, startPosition, endPosition);
            return true;
        }
        return false;
    }
    private void addAndCheckPromotion(ChessBoard board, List<ChessMove> moves, ChessPosition startPosition, ChessPosition endPosition) {
        if (endPosition.getRow() == 1 || endPosition.getRow() == 8) {
            moves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.QUEEN));
            moves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.KNIGHT));
            moves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.ROOK));
            moves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.BISHOP));
        }
        else {
            moves.add(new ChessMove(startPosition, endPosition));
        }
    }
}
