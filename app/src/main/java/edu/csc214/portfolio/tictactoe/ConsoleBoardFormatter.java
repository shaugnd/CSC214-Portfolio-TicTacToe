package edu.csc214.portfolio.tictactoe;

import java.util.Objects;

/**
 * Formats a board for display in a text-based interface.
 *
 * <p>Available cells display their user-facing cell numbers. Occupied cells
 * display symbols supplied by the configured {@link CellSymbolProvider}.</p>
 */
public final class ConsoleBoardFormatter implements BoardFormatter {
    private final PositionMapper positionMapper;
    private final CellSymbolProvider symbolProvider;

    public ConsoleBoardFormatter(PositionMapper positionMapper, CellSymbolProvider symbolProvider) {
        this.positionMapper = Objects.requireNonNull(positionMapper, "Position mapper cannot be null.");
        this.symbolProvider = Objects.requireNonNull(symbolProvider, "Cell symbol provider cannot be null.");
    }

    @Override
    public String format(Board board) {
        Objects.requireNonNull(board, "Board cannot be null.");

        int cellWidth = determineCellWidth(board);
        String separator = createSeparator(board.getColumnCount(), cellWidth);
        StringBuilder output = new StringBuilder();

        for (int row = 0; row < board.getRowCount(); row++) {
            if (row > 0) {
                output.append(System.lineSeparator()).append(separator).append(System.lineSeparator());
            }

            for (int column = 0; column < board.getColumnCount(); column++) {
                if (column > 0) {
                    output.append(" | ");
                }

                Position position = new Position(row, column);
                String displayValue = getDisplayValue(board, position);
                output.append(String.format("%" + cellWidth + "s", displayValue));
            }
        }

        return output.toString();
    }

    private String getDisplayValue(Board board, Position position) {
        CellState state = board.getCell(position);

        if (state == CellState.EMPTY) {
            return String.valueOf(positionMapper.toCellNumber(position, board));
        }

        return symbolProvider.getSymbol(state);
    }

    private int determineCellWidth(Board board) {
        int largestCellNumber = board.getRowCount() * board.getColumnCount();
        int width = String.valueOf(largestCellNumber).length();

        for (CellState state : CellState.values()) {
            width = Math.max(width, symbolProvider.getSymbol(state).length());
        }

        return width;
    }

    private String createSeparator(int columnCount, int cellWidth) {
        String cellSeparator = "-".repeat(cellWidth);
        StringBuilder separator = new StringBuilder();

        for (int column = 0; column < columnCount; column++) {
            if (column > 0) {
                separator.append("-+-");
            }

            separator.append(cellSeparator);
        }

        return separator.toString();
    }
}
