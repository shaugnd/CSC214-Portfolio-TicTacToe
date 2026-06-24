package edu.csc214.portfolio.tictactoe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests classic Tic-Tac-Toe outcome evaluation for rows, columns, diagonals,
 * incomplete games, draws, and invalid boards.
 *
 * <p>The test cases follow the ZOMBIE method: Zero, One, Many, Boundary,
 * Interface, and Exception.</p>
 */
class ClassicTicTacToeRulesTest {
    // I am not testing for invalid game boards/histories.  For example,
    // a manually created gameboard could have winning lines for both players
    // but under normal controlled game play, that cannot happen.  It will be 
    // up to GameEngine to prevent illegal play after a win.  This class is not
    // responsible for that.
    
    private final GameRules rules = new ClassicTicTacToeRules();

    // Zero

    @Test
    void emptyBoardIsInProgress() {
        Board board = new GridBoard(3, 3);

        assertEquals(GameOutcome.IN_PROGRESS, rules.evaluate(board));
    }

    // One

    @Test
    void boardWithOneMoveIsInProgress() {
        Board board = new GridBoard(3, 3);
        board.place(new Position(1, 1), CellState.PLAYER_ONE);

        assertEquals(GameOutcome.IN_PROGRESS, rules.evaluate(board));
    }

    // Many

    @Test
    void detectsPlayerOneWinInEveryRow() {
        for (int row = 0; row < 3; row++) {
            Board board = new GridBoard(3, 3);
            fillRow(board, row, CellState.PLAYER_ONE);

            assertEquals(GameOutcome.PLAYER_ONE_WINS, rules.evaluate(board), "Failed to detect player one win in row " + row);
        }
    }

    @Test
    void detectsPlayerTwoWinInEveryRow() {
        for (int row = 0; row < 3; row++) {
            Board board = new GridBoard(3, 3);
            fillRow(board, row, CellState.PLAYER_TWO);

            assertEquals(GameOutcome.PLAYER_TWO_WINS, rules.evaluate(board), "Failed to detect player two win in row " + row);
        }
    }

    @Test
    void detectsPlayerOneWinInEveryColumn() {
        for (int column = 0; column < 3; column++) {
            Board board = new GridBoard(3, 3);
            fillColumn(board, column, CellState.PLAYER_ONE);

            assertEquals(GameOutcome.PLAYER_ONE_WINS, rules.evaluate(board), "Failed to detect player one win in column " + column);
        }
    }

    @Test
    void detectsPlayerTwoWinInEveryColumn() {
        for (int column = 0; column < 3; column++) {
            Board board = new GridBoard(3, 3);
            fillColumn(board, column, CellState.PLAYER_TWO);

            assertEquals(GameOutcome.PLAYER_TWO_WINS, rules.evaluate(board), "Failed to detect player two win in column " + column);
        }
    }

    @Test
    void detectsPlayerOneWinOnMainDiagonal() {
        Board board = new GridBoard(3, 3);
        place(board, CellState.PLAYER_ONE, new Position(0, 0), new Position(1, 1), new Position(2, 2));

        assertEquals(GameOutcome.PLAYER_ONE_WINS, rules.evaluate(board));
    }

    @Test
    void detectsPlayerTwoWinOnOppositeDiagonal() {
        Board board = new GridBoard(3, 3);
        place(board, CellState.PLAYER_TWO, new Position(0, 2), new Position(1, 1), new Position(2, 0));

        assertEquals(GameOutcome.PLAYER_TWO_WINS, rules.evaluate(board));
    }

    // Boundary

    @Test
    void fullBoardWithoutWinnerIsDraw() {
        Board board = createDrawBoard();

        assertEquals(GameOutcome.DRAW, rules.evaluate(board));
    }

    @Test
    void nearlyFullBoardWithoutWinnerIsInProgress() {
        Board board = new GridBoard(3, 3);
        place(board, CellState.PLAYER_ONE, new Position(0, 0), new Position(0, 2), new Position(1, 0), new Position(2, 1));
        place(board, CellState.PLAYER_TWO, new Position(0, 1), new Position(1, 1), new Position(1, 2), new Position(2, 0));

        assertEquals(GameOutcome.IN_PROGRESS, rules.evaluate(board));
    }

    @Test
    void fullBoardWithWinnerIsWinRatherThanDraw() {
        Board board = new GridBoard(3, 3);
        place(board, CellState.PLAYER_ONE, new Position(0, 0), new Position(0, 1), new Position(0, 2), new Position(1, 2), new Position(2, 1));
        place(board, CellState.PLAYER_TWO, new Position(1, 0), new Position(1, 1), new Position(2, 0), new Position(2, 2));

        assertEquals(GameOutcome.PLAYER_ONE_WINS, rules.evaluate(board));
    }

    // Interface

    @Test
    void evaluatesBoardThroughInterfaces() {
        Board board = new GridBoard(3, 3);
        GameRules gameRules = new ClassicTicTacToeRules();
        fillColumn(board, 2, CellState.PLAYER_TWO);

        assertEquals(GameOutcome.PLAYER_TWO_WINS, gameRules.evaluate(board));
    }

    @Test
    void evaluationDoesNotModifyBoard() {
        Board board = new GridBoard(3, 3);
        board.place(new Position(0, 0), CellState.PLAYER_ONE);
        board.place(new Position(1, 1), CellState.PLAYER_TWO);
        List<Position> availableBefore = board.getAvailablePositions();

        rules.evaluate(board);

        assertEquals(CellState.PLAYER_ONE, board.getCell(new Position(0, 0)));
        assertEquals(CellState.PLAYER_TWO, board.getCell(new Position(1, 1)));
        assertEquals(availableBefore, board.getAvailablePositions());
    }

    // Exception

    @Test
    void rejectsNullBoard() {
        assertThrows(NullPointerException.class, () -> rules.evaluate(null));
    }

    @Test
    void rejectsBoardWithWrongRowCount() {
        Board board = new GridBoard(2, 3);

        assertThrows(IllegalArgumentException.class, () -> rules.evaluate(board));
    }

    @Test
    void rejectsBoardWithWrongColumnCount() {
        Board board = new GridBoard(3, 4);

        assertThrows(IllegalArgumentException.class, () -> rules.evaluate(board));
    }

    private static void fillRow(Board board, int row, CellState state) {
        for (int column = 0; column < 3; column++) {
            board.place(new Position(row, column), state);
        }
    }

    private static void fillColumn(Board board, int column, CellState state) {
        for (int row = 0; row < 3; row++) {
            board.place(new Position(row, column), state);
        }
    }

    private static void place(Board board, CellState state, Position... positions) {
        for (Position position : positions) {
            board.place(position, state);
        }
    }

    private static Board createDrawBoard() {
        Board board = new GridBoard(3, 3);
        place(board, CellState.PLAYER_ONE, new Position(0, 0), new Position(0, 2), new Position(1, 0), new Position(2, 1), new Position(2, 2));
        place(board, CellState.PLAYER_TWO, new Position(0, 1), new Position(1, 1), new Position(1, 2), new Position(2, 0));
        return board;
    }
}
