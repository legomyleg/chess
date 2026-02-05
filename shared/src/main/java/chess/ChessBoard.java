package chess;

import java.util.*;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard implements Iterable<ChessBoard.PieceAtPosition> {
    private ChessPiece[][] board;

    public ChessBoard() {
        board = new ChessPiece[8][8];
    }

    public ChessBoard(ChessBoard oldBoard) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {

                int r = i+1;
                int c = j+1;
                ChessPosition currPosition = new ChessPosition(r, c);

                if (oldBoard.getPiece(currPosition) != null) {
                    board[i][j] = new ChessPiece(oldBoard.getPiece(currPosition));
                }

                board[i][j] = null;

            }
        }
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow() - 1][position.getColumn() - 1];
    }

    public boolean checkClear(ChessPosition position) {
        if (position.outOfBounds()) {
            return false;
        }
        return (this.getPiece(position) == null);
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        board = new ChessPiece[8][8];

        for (int i = 0; i < 8; i++) {
            board[1][i] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            board[6][i] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        }

        board[0][0] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        board[0][1] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        board[0][2] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        board[0][3] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        board[0][4] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
        board[0][5] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        board[0][6] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        board[0][7] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);

        for (int i = 0; i < 8; i++) {
            board[7][i] = new ChessPiece(ChessGame.TeamColor.BLACK, board[0][i].getPieceType());
        }
    }

    public static class PieceAtPosition {
        private final ChessPiece piece;
        private final ChessPosition position;

        public PieceAtPosition(ChessPiece piece, ChessPosition position) {
            this.piece = piece;
            this.position = position;
        }

        public ChessPiece getPiece() {
            return piece;
        }

        public ChessPosition getPosition() {
            return position;
        }
    }

    @Override
    public Iterator<PieceAtPosition> iterator() {

        List<PieceAtPosition> pieces = new ArrayList<>();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] != null) {
                    ChessPosition position = new ChessPosition(i + 1, j + 1);
                    PieceAtPosition piece = new PieceAtPosition(getPiece(position), position);
                    pieces.add(piece);
                }
            }
        }

        return pieces.iterator();
    }

    @Override
    public String toString() {
        String boardLayout = "";
        for (int i = 7; i >= 0; i--) {

            for (int j = 0; j < 8; j++) {
                boardLayout = boardLayout.concat("|");
                if (board[i][j] != null) {
                    boardLayout = boardLayout.concat(board[i][j].toString());
                }
                else {
                    boardLayout = boardLayout.concat(" ");
                }
                boardLayout = (j == 7) ? boardLayout.concat("|\n") : boardLayout;
            }
        }

        return boardLayout;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }
}
