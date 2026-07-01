package edu.csc214.portfolio.tictactoe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;

/**
 * Verifies the opportunistic computer player's rule order, legal move
 * selection, player-state handling, and defensive behavior.
 *
 * <p>The test cases follow the ZOMBIE method: Zero, One, Many, Boundary,
 * Interface, and Exception.</p>
 */
class OpportunisticComputerPlayerTest {
    private static final GameRules RULES = new ClassicTicTacToeRules();

    // Zero
    @Test
    void emptyBoardProducesACornerMove() {
        Board board = new GridBoard(3, 3);
        Player computer = computer(CellState.PLAYER_ONE, 1);

        Position move = computer.chooseMove(board);

        assertTrue(List.of(
                new Position(0, 0),
                new Position(0, 2),
                new Position(2, 0),
                new Position(2, 2)).contains(move));
    }

    // One
    @Test
    void secondMoveUsesCenterWhenAvailable() {
        Board board = new GridBoard(3, 3);
        board.place(new Position(0, 0), CellState.PLAYER_ONE);
        Player computer = computer(CellState.PLAYER_TWO, 2);

        assertEquals(new Position(1, 1), computer.chooseMove(board));
    }

    @Test
    void secondMoveRemainsLegalWhenCenterIsOccupied() {
        Board board = new GridBoard(3, 3);
        board.place(new Position(1, 1), CellState.PLAYER_ONE);
        Player computer = computer(CellState.PLAYER_TWO, 3);

        Position move = computer.chooseMove(board);

        assertTrue(board.isAvailable(move));
        assertFalse(move.equals(new Position(1, 1)));
    }

    // Many
    @Test
    void immediateWinningMoveIsTaken() {
        Board board = new GridBoard(3, 3);
        board.place(new Position(0, 0), CellState.PLAYER_ONE);
        board.place(new Position(0, 1), CellState.PLAYER_ONE);
        board.place(new Position(1, 1), CellState.PLAYER_TWO);
        Player computer = computer(CellState.PLAYER_ONE, 4);

        assertEquals(new Position(0, 2), computer.chooseMove(board));
    }

    @Test
    void winningMoveTakesPriorityOverBlockingMove() {
        Board board = new GridBoard(3, 3);
        board.place(new Position(0, 0), CellState.PLAYER_ONE);
        board.place(new Position(0, 1), CellState.PLAYER_ONE);
        board.place(new Position(1, 0), CellState.PLAYER_TWO);
        board.place(new Position(1, 1), CellState.PLAYER_TWO);
        Player computer = computer(CellState.PLAYER_ONE, 5);

        assertEquals(new Position(0, 2), computer.chooseMove(board));
    }

    @Test
    void immediateOpponentWinIsBlocked() {
        Board board = new GridBoard(3, 3);
        board.place(new Position(0, 0), CellState.PLAYER_ONE);
        board.place(new Position(0, 1), CellState.PLAYER_ONE);
        board.place(new Position(1, 1), CellState.PLAYER_TWO);
        Player computer = computer(CellState.PLAYER_TWO, 6);

        assertEquals(new Position(0, 2), computer.chooseMove(board));
    }

    @Test
    void fallbackMoveIsRandomAndLegal() {
        Board board = new GridBoard(3, 3);
        board.place(new Position(0, 0), CellState.PLAYER_ONE);
        board.place(new Position(1, 1), CellState.PLAYER_TWO);
        board.place(new Position(2, 2), CellState.PLAYER_ONE);
        Player computer = computer(CellState.PLAYER_TWO, 7);

        Position move = computer.chooseMove(board);

        assertTrue(board.isAvailable(move));
    }

    // Boundary
    @Test
    void playerTwoCanTakeAnImmediateWinningMove() {
        Board board = new GridBoard(3, 3);
        board.place(new Position(0, 0), CellState.PLAYER_ONE);
        board.place(new Position(2, 0), CellState.PLAYER_TWO);
        board.place(new Position(2, 1), CellState.PLAYER_TWO);
        Player computer = computer(CellState.PLAYER_TWO, 8);

        assertEquals(new Position(2, 2), computer.chooseMove(board));
    }

    @Test
    void fullBoardIsRejected() {
        Board board = fullBoard();
        Player computer = computer(CellState.PLAYER_ONE, 9);

        assertThrows(IllegalStateException.class, () -> computer.chooseMove(board));
    }

    // Interface
    @Test
    void choosingMoveDoesNotModifySuppliedBoard() {
        Board board = new GridBoard(3, 3);
        board.place(new Position(0, 0), CellState.PLAYER_ONE);
        board.place(new Position(0, 1), CellState.PLAYER_ONE);
        board.place(new Position(1, 1), CellState.PLAYER_TWO);
        List<Position> availableBefore = board.getAvailablePositions();
        Player computer = computer(CellState.PLAYER_ONE, 10);

        computer.chooseMove(board);

        assertEquals(availableBefore, board.getAvailablePositions());
        assertEquals(CellState.PLAYER_ONE, board.getCell(new Position(0, 0)));
        assertEquals(CellState.PLAYER_ONE, board.getCell(new Position(0, 1)));
        assertEquals(CellState.PLAYER_TWO, board.getCell(new Position(1, 1)));
        assertEquals(CellState.EMPTY, board.getCell(new Position(0, 2)));
    }

    // Exception
    @Test
    void nullBoardIsRejected() {
        Player computer = computer(CellState.PLAYER_ONE, 11);

        assertThrows(NullPointerException.class, () -> computer.chooseMove(null));
    }

    @Test
    void nullRulesAreRejected() {
        assertThrows(
                NullPointerException.class,
                () -> new OpportunisticComputerPlayer(
                        "Computer",
                        CellState.PLAYER_ONE,
                        null,
                        new Random(12)));
    }

    @Test
    void nullRandomSourceIsRejected() {
        assertThrows(
                NullPointerException.class,
                () -> new OpportunisticComputerPlayer(
                        "Computer",
                        CellState.PLAYER_ONE,
                        RULES,
                        null));
    }

    private static Player computer(CellState state, long seed) {
        return new OpportunisticComputerPlayer("Computer", state, RULES, new Random(seed));
    }

    private static Board fullBoard() {
        Board board = new GridBoard(3, 3);
        CellState[][] states = {
                {CellState.PLAYER_ONE, CellState.PLAYER_TWO, CellState.PLAYER_ONE},
                {CellState.PLAYER_ONE, CellState.PLAYER_TWO, CellState.PLAYER_TWO},
                {CellState.PLAYER_TWO, CellState.PLAYER_ONE, CellState.PLAYER_ONE}
        };

        for (int row = 0; row < states.length; row++) {
            for (int column = 0; column < states[row].length; column++) {
                board.place(new Position(row, column), states[row][column]);
            }
        }

        return board;
    }
}
