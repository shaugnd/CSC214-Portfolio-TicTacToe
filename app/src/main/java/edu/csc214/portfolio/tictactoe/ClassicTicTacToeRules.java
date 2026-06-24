package edu.csc214.portfolio.tictactoe;

import java.util.Objects;

/**
 * Evaluates the outcome of a classic three-by-three Tic-Tac-Toe board.
 *
 * <p>A player wins by occupying all three cells in any row, column, or
 * diagonal. The rule set examines the board without modifying it.</p>
 */
public final class ClassicTicTacToeRules implements GameRules {
    // This class evaluates ONLY the classic 3.3 game.  A larger board or different
    // winning requirement should receive a new GameRules implementat4ion rather than
    // modifying this class.

    private static final int BOARD_SIZE = 3;

    @Override
    public GameOutcome evaluate(Board board) {
        Objects.requireNonNull(board, "Board cannot be null.");
        validateBoardDimensions(board);

        CellState winner = findWinner(board);

        if (winner == CellState.PLAYER_ONE) {
            return GameOutcome.PLAYER_ONE_WINS;
        }

        if (winner == CellState.PLAYER_TWO) {
            return GameOutcome.PLAYER_TWO_WINS;
        }

        return board.isFull() ? GameOutcome.DRAW : GameOutcome.IN_PROGRESS;
    }

    private CellState findWinner(Board board) {
        for (int index = 0; index < BOARD_SIZE; index++) {
            CellState rowWinner = getLineWinner(board, new Position(index, 0), 
                                                new Position(index, 1), 
                                                new Position(index, 2)
                                            );

            if (rowWinner != CellState.EMPTY) {
                return rowWinner;
            }

            CellState columnWinner = getLineWinner(board, new Position(0, index), 
                                                   new Position(1, index), 
                                                   new Position(2, index)
                                                );

            if (columnWinner != CellState.EMPTY) {
                return columnWinner;
            }
        }

        CellState mainDiagonalWinner = getLineWinner(board, new Position(0, 0), 
                                                     new Position(1, 1), 
                                                     new Position(2, 2)
                                                );

        if (mainDiagonalWinner != CellState.EMPTY) {
            return mainDiagonalWinner;
        }

        return getLineWinner(board, new Position(0, 2), 
                             new Position(1, 1), 
                             new Position(2, 0)
                            );
    }

    private CellState getLineWinner(Board board, Position first, Position second, Position third) {
        CellState firstState = board.getCell(first);

        if (firstState != CellState.EMPTY 
            && firstState == board.getCell(second) 
            && firstState == board.getCell(third)) {
            return firstState;
        }

        return CellState.EMPTY;
    }

    private static void validateBoardDimensions(Board board) {
        if (board.getRowCount() != BOARD_SIZE || board.getColumnCount() != BOARD_SIZE) {
            throw new IllegalArgumentException("Classic Tic-Tac-Toe requires a 3-by-3 board.");
        }
    }
}
