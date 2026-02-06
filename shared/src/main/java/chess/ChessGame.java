package chess;

import chess.PieceMoves.MoveCalculator;

import java.util.*;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamTurn;
    private ChessBoard board;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        teamTurn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {

        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            throw new RuntimeException("Empty position passed to validMoves.");
        }

        Collection<ChessMove> possibleMoves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();

        for (ChessMove move : possibleMoves) {

            ChessPiece alteredPiece = board.getPiece(move.getEndPosition());

            board.addPiece(move.getStartPosition(), null);
            board.addPiece(move.getEndPosition(), piece);

            if (!isInCheck(piece.getTeamColor())) {
                validMoves.add(move);
            }

            board.addPiece(move.getStartPosition(), piece);
            board.addPiece(move.getEndPosition(), alteredPiece);
        }

        if (piece.getPieceType() == ChessPiece.PieceType.KING) {

            TeamColor teamColor = piece.getTeamColor();
            int teamRow = (teamColor == TeamColor.WHITE) ? 1 : 8;

            if (canCastleLeft(teamColor)) {
                validMoves.add(new ChessMove(startPosition, new ChessPosition(teamRow, 3)));
            }
            if (canCastleRight(teamColor)) {
                validMoves.add(new ChessMove(startPosition, new ChessPosition(teamRow, 7)));
            }
        }

        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {

        ChessPiece startPiece = board.getPiece(move.getStartPosition());
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();

        if (startPiece == null) {
            throw new InvalidMoveException("There is no piece at %s.".formatted(move.getStartPosition()));
        } else if (startPiece.getTeamColor() != teamTurn) {
            throw new InvalidMoveException("Not %s's turn. Invalid move.".formatted(startPiece.getTeamColor()));
        }

        if ((startPiece.getPieceType() == ChessPiece.PieceType.KING) && Math.abs(startPosition.getColumn() - endPosition.getColumn()) > 1) {
            if (move.getEndPosition().getColumn() < move.getStartPosition().getColumn()) {
                castleLeft(startPiece.getTeamColor());
            } else {
                castleRight(startPiece.getTeamColor());
            }
            return;
        }

        if (move.getPromotionPiece() == null) {
            board.addPiece(move.getEndPosition(), startPiece);
            startPiece.setMoved();
        } else {
            ChessPiece promotedPiece = new ChessPiece(startPiece.getTeamColor(), move.getPromotionPiece());
            board.addPiece(move.getEndPosition(), promotedPiece);
            promotedPiece.setMoved();
        }

        board.addPiece(move.getStartPosition(), null);
        teamTurn = (teamTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {

        TeamColor opponentColor = (teamColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;

        List<ChessPosition> kingPositionArray = getPiecePositions(ChessPiece.PieceType.KING, teamColor);
        ChessPosition kingPosition = kingPositionArray.get(0);

        Collection<ChessMove> attacks = teamAttacks(opponentColor);

        for (ChessMove attack : attacks) {
            if (attack.getEndPosition().equals(kingPosition)) {
                return true;
            }
        }

        return false;

    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return isInCheck(teamColor) && (validTeamMoves(teamColor).isEmpty());
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return !isInCheck(teamColor) && (validTeamMoves(teamColor).isEmpty());
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    public List<ChessPosition> getPiecePositions(ChessPiece.PieceType type, TeamColor color) {
        List<ChessPosition> positionList = new ArrayList<>();

        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {

                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);

                if (piece == null) {
                    continue;
                }

                if (piece.getPieceType() == type && piece.getTeamColor() == color) {
                    positionList.add(position);
                }

            }
        }

        return positionList;
    }

    public ChessPosition getSinglePiece(ChessPiece.PieceType type, TeamColor color) {
        List<ChessPosition> pieceArray = getPiecePositions(type, color);
        if (pieceArray.isEmpty()) {
            throw new RuntimeException("Error in getSinglePiece. Piece %s, %s not found.".formatted(type, color));
        }
        return pieceArray.get(0);
    }

    public Collection<ChessMove> teamAttacks(TeamColor team) {
        List<ChessMove> moves = new ArrayList<>();

        for (ChessBoard.PieceAtPosition piece : board) {
            if (piece.getPiece() != null && piece.getPiece().getTeamColor() == team) {
                moves.addAll(piece.getPiece().pieceMoves(board, piece.getPosition()));
            }
        }

        return moves;
    }

    public Collection<ChessMove> validTeamMoves(TeamColor team) {
        List<ChessMove> moves = new ArrayList<>();

        for (ChessPosition position : ChessPosition.positions()) {
            if (board.getPiece(position) != null && board.getPiece(position).getTeamColor() == team) {
                moves.addAll(validMoves(position));
            }
        }

        return moves;
    }

    public void addEnPassantMoves(ChessBoard board, Collection<ChessMove> moves, ChessPosition startPosition) {
        throw new RuntimeException("Not Implemented");
    }

    public void addCastlingMoves(ChessBoard board, Collection<ChessMove> moves, ChessPosition startPosition) {
        throw new RuntimeException("Not Implemented");
    }

    public boolean canCastleLeft(TeamColor teamColor) {
        int teamRow = (teamColor == TeamColor.WHITE) ? 1 : 8;
        ChessPosition rookPosition = new ChessPosition(teamRow, 1);
        ChessPosition kingPosition = new ChessPosition(teamRow, 5);
        TeamColor opponentColor = (teamColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;

        ChessPiece rookPiece = board.getPiece(rookPosition);
        ChessPiece kingPiece = board.getPiece(kingPosition);

        Collection<ChessMove> opponentAttacks = teamAttacks(opponentColor);
        List<ChessPosition> attackedSpots = new ArrayList<>();
        for (ChessMove move : opponentAttacks) {
            attackedSpots.add(move.getEndPosition());
        }

        if (rookPiece == null || rookPiece.getPieceType() != ChessPiece.PieceType.ROOK) {
            return false;
        }
        if (kingPiece == null || kingPiece.getPieceType() != ChessPiece.PieceType.KING) {
            return false;
        }
        if (kingPiece.hasMoved() || rookPiece.hasMoved() || isInCheck(teamColor)) {
            return false;
        }

        for (int column = kingPosition.getColumn() - 1; column > 1; column--) {
            ChessPosition position = new ChessPosition(teamRow, column);
            if (!board.checkClear(position)) {
                return false;
            }
        }

        ChessPosition oneOver = new ChessPosition(teamRow, kingPosition.getColumn() - 1);
        ChessPosition twoOver = new ChessPosition(teamRow, kingPosition.getColumn() - 2);
        if (attackedSpots.contains(oneOver) || attackedSpots.contains(twoOver)) {
            return false;
        }

        return true;

    }

    public boolean canCastleRight(TeamColor teamColor) {
        int teamRow = (teamColor == TeamColor.WHITE) ? 1 : 8;
        ChessPosition rookPosition = new ChessPosition(teamRow, 8);
        ChessPosition kingPosition = new ChessPosition(teamRow, 5);
        TeamColor opponentColor = (teamColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;

        ChessPiece rookPiece = board.getPiece(rookPosition);
        ChessPiece kingPiece = board.getPiece(kingPosition);

        Collection<ChessMove> opponentAttacks = teamAttacks(opponentColor);
        List<ChessPosition> attackedSpots = new ArrayList<>();
        for (ChessMove move : opponentAttacks) {
            attackedSpots.add(move.getEndPosition());
        }

        if (rookPiece == null || rookPiece.getPieceType() != ChessPiece.PieceType.ROOK || attackedSpots.contains(rookPosition)) {
            return false;
        }
        if (kingPiece == null || kingPiece.getPieceType() != ChessPiece.PieceType.KING) {
            return false;
        }
        if (kingPiece.hasMoved() || rookPiece.hasMoved() || isInCheck(teamColor)) {
            return false;
        }

        for (int column = kingPosition.getColumn() + 1; column < 8; column++) {
            ChessPosition position = new ChessPosition(teamRow, column);
            if (!board.checkClear(position)) {
                return false;
            }
        }

        ChessPosition oneOver = new ChessPosition(teamRow, kingPosition.getColumn() + 1);
        ChessPosition twoOver = new ChessPosition(teamRow, kingPosition.getColumn() + 2);
        if (attackedSpots.contains(oneOver) || attackedSpots.contains(twoOver)) {
            return false;
        }

        return true;

    }

    public void castleRight(TeamColor teamColor) {
        int teamRow = (teamColor == TeamColor.WHITE) ? 1 : 8;
        ChessPosition rookPosition = new ChessPosition(teamRow, 8);
        ChessPosition kingPosition = new ChessPosition(teamRow, 5);
        ChessPiece rookPiece = board.getPiece(rookPosition);
        ChessPiece kingPiece = board.getPiece(kingPosition);

        board.addPiece(kingPosition, null);
        board.addPiece(new ChessPosition(teamRow, 7), kingPiece);

        board.addPiece(rookPosition, null);
        board.addPiece(new ChessPosition(teamRow, 6), rookPiece);

        kingPiece.setMoved();
        rookPiece.setMoved();

        teamTurn = (teamColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    public void castleLeft(TeamColor teamColor) {
        int teamRow = (teamColor == TeamColor.WHITE) ? 1 : 8;
        ChessPosition rookPosition = new ChessPosition(teamRow, 1);
        ChessPosition kingPosition = new ChessPosition(teamRow, 5);
        ChessPiece rookPiece = board.getPiece(rookPosition);
        ChessPiece kingPiece = board.getPiece(kingPosition);

        board.addPiece(kingPosition, null);
        board.addPiece(new ChessPosition(teamRow, 3), kingPiece);

        board.addPiece(rookPosition, null);
        board.addPiece(new ChessPosition(teamRow, 4), rookPiece);

        kingPiece.setMoved();
        rookPiece.setMoved();

        teamTurn = (teamColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, board);
    }
}
