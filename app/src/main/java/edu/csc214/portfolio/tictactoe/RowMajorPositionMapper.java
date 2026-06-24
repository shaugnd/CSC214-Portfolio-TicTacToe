package edu.csc214.portfolio.tictactoe;

import java.util.Objects;

/**
 * Maps board positions to consecutive cell numbers in row-major order.
 *
 * <p>Numbering begins at one in the upper-left cell, proceeds from left to
 * right, and continues on the next row.</p>
 */
public final class RowMajorPositionMapper implements PositionMapper {
    // This left to right, top to bottom numbering implementation of PositionMapper
    // will work with any rectangular board whose total cell count fits within an int.

    @Override
    public int toCellNumber(Position position, Board board) {
        Objects.requireNonNull(position, "Position cannot be null.");
        Objects.requireNonNull(board, "Board cannot be null.");

        if (!board.isWithinBounds(position)) {
            throw new IndexOutOfBoundsException("Position is outside the board boundaries.");
        }

        return position.row() * board.getColumnCount() + position.column() + 1;
    }

    @Override
    public Position toPosition(int cellNumber, Board board) {
        Objects.requireNonNull(board, "Board cannot be null.");

        long cellCount = (long) board.getRowCount() * board.getColumnCount();

        if (cellNumber < 1 || cellNumber > cellCount) {
            throw new IndexOutOfBoundsException("Cell number is outside the board boundaries.");
        }

        int zeroBasedCell = cellNumber - 1;
        int row = zeroBasedCell / board.getColumnCount();
        int column = zeroBasedCell % board.getColumnCount();

        return new Position(row, column);
    }
}
