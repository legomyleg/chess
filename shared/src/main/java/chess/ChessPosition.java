package chess;

import java.util.*;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {
    private int row;
    private int col;

    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return col;
    }

    public boolean outOfBounds() {
        return !(row >= 1 && row <= 8 && col >= 1 && col <= 8);
    }

    public static Iterable<ChessPosition> positions() {

        return () -> new Iterator<ChessPosition>() {
            private int row = 1;
            private int col = 1;

            @Override
            public boolean hasNext() {
                return row <= 8;
            }

            @Override
            public ChessPosition next() {
                if (!hasNext()) throw new NoSuchElementException();
                ChessPosition position = new ChessPosition(row, col);
                col++;
                if (col == 9) {col = 1; row++;}
                return position;
            }
        };
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPosition that = (ChessPosition) o;
        return row == that.row && col == that.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    @Override
    public String toString() {
        return String.format("[%d, %d]", row, col);
    }


}
