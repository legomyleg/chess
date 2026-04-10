package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Collection;

import static ui.EscapeSequences.*;

public final class BoardRenderer {

    private static final String LIGHT_SQUARE_BG = SET_BG_COLOR_WHITE;
    private static final String DARK_SQUARE_BG = SET_BG_COLOR_BLACK;
    private static final String HIGHLIGHT_SQUARE_BG = SET_BG_COLOR_GREEN;
    private static final String SELECTED_SQUARE_BG = SET_BG_COLOR_YELLOW;
    private static final String WHITE_PIECE_COLOR = SET_TEXT_COLOR_RED;
    private static final String BLACK_PIECE_COLOR = SET_TEXT_COLOR_BLUE;
    private static final String COORDINATE_COLOR = SET_TEXT_BOLD + SET_TEXT_COLOR_BLACK;

    private BoardRenderer() {
    }

    public static String render(ChessGame game, ChessGame.TeamColor perspective, ChessPosition selectedPosition,
                                Collection<ChessPosition> highlightedSquares) {
        ChessBoard board = game.getBoard();
        int[] rowOrder = perspective == ChessGame.TeamColor.BLACK
                ? new int[]{1, 2, 3, 4, 5, 6, 7, 8}
                : new int[]{8, 7, 6, 5, 4, 3, 2, 1};
        int[] colOrder = perspective == ChessGame.TeamColor.BLACK
                ? new int[]{8, 7, 6, 5, 4, 3, 2, 1}
                : new int[]{1, 2, 3, 4, 5, 6, 7, 8};

        String lineSeparator = System.lineSeparator();
        StringBuilder output = new StringBuilder();
        output.append(lineSeparator);
        output.append(renderColumnLabels(colOrder)).append(lineSeparator);

        for (int row : rowOrder) {
            StringBuilder line = new StringBuilder();
            line.append(COORDINATE_COLOR).append(" ").append(row).append(" ").append(RESET_ALL);

            for (int col : colOrder) {
                line.append(renderSquare(board, row, col, selectedPosition, highlightedSquares));
            }

            line.append(COORDINATE_COLOR).append(" ").append(row).append(" ").append(RESET_ALL);
            output.append(line).append(lineSeparator);
        }

        output.append(renderColumnLabels(colOrder)).append(lineSeparator);
        return output.toString();
    }

    private static String renderColumnLabels(int[] colOrder) {
        StringBuilder labels = new StringBuilder();
        labels.append(COORDINATE_COLOR).append("   ");

        for (int col : colOrder) {
            labels.append(" ").append((char) ('a' + col - 1)).append(" ");
        }

        return labels.append(RESET_ALL).toString();
    }

    private static String renderSquare(ChessBoard board, int row, int col, ChessPosition selectedPosition,
                                       Collection<ChessPosition> highlightedSquares) {
        ChessPosition position = new ChessPosition(row, col);
        ChessPiece piece = board.getPiece(new ChessPosition(row, col));
        String squareColor = getSquareColor(position, selectedPosition, highlightedSquares);
        String pieceColor = piece == null
                ? RESET_TEXT_COLOR
                : (piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_PIECE_COLOR : BLACK_PIECE_COLOR);

        return squareColor + pieceColor + pieceSymbol(piece) + RESET_ALL;
    }

    private static String getSquareColor(ChessPosition position, ChessPosition selectedPosition,
                                         Collection<ChessPosition> highlightedSquares) {
        if (position.equals(selectedPosition)) {
            return SELECTED_SQUARE_BG;
        }
        if (highlightedSquares.contains(position)) {
            return HIGHLIGHT_SQUARE_BG;
        }
        return ((position.getRow() + position.getColumn()) % 2 == 0) ? DARK_SQUARE_BG : LIGHT_SQUARE_BG;
    }

    private static String pieceSymbol(ChessPiece piece) {
        if (piece == null) {
            return EMPTY;
        }

        return switch (piece.getTeamColor()) {
            case WHITE -> switch (piece.getPieceType()) {
                case KING -> WHITE_KING;
                case QUEEN -> WHITE_QUEEN;
                case BISHOP -> WHITE_BISHOP;
                case KNIGHT -> WHITE_KNIGHT;
                case ROOK -> WHITE_ROOK;
                case PAWN -> WHITE_PAWN;
            };
            case BLACK -> switch (piece.getPieceType()) {
                case KING -> BLACK_KING;
                case QUEEN -> BLACK_QUEEN;
                case BISHOP -> BLACK_BISHOP;
                case KNIGHT -> BLACK_KNIGHT;
                case ROOK -> BLACK_ROOK;
                case PAWN -> BLACK_PAWN;
            };
        };
    }
}
