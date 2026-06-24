package edu.csc214.portfolio.tictactoe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Stores the logical state of a rectangular game board.
 *
 * <p>The board validates dimensions, coordinates, and placements. It does not determine game outcomes, select player moves, or define how cell contents are displayed.</p>
 */
public final class GridBoard implements Board {
    // Need a max cell count to avoid out of memory errors if
    // variable board size becomes a thing down the road and 
    // a user gets cheeky.

    private static final int MAX_CELL_COUNT = 1_000_000;

    private final int rowCount;
    private final int columnCount;
    private final CellState[][] cells;

    public GridBoard(int rowCount, int columnCount) {
        // Make sure that our GridBoard dimensions are not going to 
        // break anything.
        validateDimensions(rowCount, columnCount);

        this.rowCount = rowCount;
        this.columnCount = columnCount;
        this.cells = new CellState[rowCount][columnCount];

        for (int row = 0; row < rowCount; row++) {
            for (int column = 0; column < columnCount; column++) {
                cells[row][column] = CellState.EMPTY;
            }
        }
    }

    @Override
    public int getRowCount() {
        return rowCount;
    }

    @Override
    public int getColumnCount() {
        return columnCount;
    }

    @Override
    public boolean isWithinBounds(Position position) {
        Objects.requireNonNull(position, "Position cannot be null.");
        return position.row() >= 0 && position.row() < rowCount && position.column() >= 0 && position.column() < columnCount;
    }

    @Override
    public CellState getCell(Position position) {
        validatePosition(position);
        return cells[position.row()][position.column()];
    }

    @Override
    public boolean isAvailable(Position position) {
        return isWithinBounds(position) && cells[position.row()][position.column()] == CellState.EMPTY;
    }

    @Override
    public void place(Position position, CellState state) {
        validatePosition(position);
        Objects.requireNonNull(state, "Cell state cannot be null.");

        if (state == CellState.EMPTY) {
            throw new IllegalArgumentException("Cannot place the EMPTY cell state.");
        }

        if (!isAvailable(position)) {
            throw new IllegalStateException("The selected position is already occupied.");
        }

        cells[position.row()][position.column()] = state;
    }

    @Override
    public boolean isFull() {
        return getAvailablePositions().isEmpty();
    }

    @Override
    public List<Position> getAvailablePositions() {
        List<Position> availablePositions = new ArrayList<>();

        for (int row = 0; row < rowCount; row++) {
            for (int column = 0; column < columnCount; column++) {
                if (cells[row][column] == CellState.EMPTY) {
                    availablePositions.add(new Position(row, column));
                }
            }
        }

        return List.copyOf(availablePositions);
    }

    @Override
    public Board copy() {
        GridBoard copy = new GridBoard(rowCount, columnCount);

        for (int row = 0; row < rowCount; row++) {
            System.arraycopy(cells[row], 0, copy.cells[row], 0, columnCount);
        }

        return copy;
    }

    private static void validateDimensions(int rowCount, int columnCount) {
        if (rowCount <= 0 || columnCount <= 0) {
            throw new IllegalArgumentException("Board dimensions must be greater than zero.");
        }

        long cellCount = (long) rowCount * columnCount;

        if (cellCount > MAX_CELL_COUNT) {
            throw new IllegalArgumentException("Board cannot contain more than " + MAX_CELL_COUNT + " cells.");
        }
    }

    private void validatePosition(Position position) {
        if (!isWithinBounds(position)) {
            throw new IndexOutOfBoundsException("Position is outside the board boundaries.");
        }
    }
}

