package edu.csc214.portfolio.tictactoe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests the dimensions, occupancy, placement, copying, and defensive behavior
 * of {@link GridBoard}.
 *
 * <p>The test cases follow the ZOMBIE method: Zero, One, Many, Boundary,
 * Interface, and Exception.</p>
 */
class GridBoardTest {

    // Zero

    @Test
    void newBoardContainsZeroOccupiedCells() {
        Board board = new GridBoard(3, 3);

        assertEquals(9, board.getAvailablePositions().size());
        assertFalse(board.isFull());

        for (Position position : board.getAvailablePositions()) {
            assertEquals(CellState.EMPTY, board.getCell(position));
        }
    }

    @Test
    void zeroRowsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new GridBoard(0, 3));
    }

    @Test
    void zeroColumnsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new GridBoard(3, 0));
    }

    // One

    @Test
    void oneByOneBoardStartsEmpty() {
        Board board = new GridBoard(1, 1);

        assertEquals(1, board.getRowCount());
        assertEquals(1, board.getColumnCount());
        assertEquals(CellState.EMPTY, board.getCell(new Position(0, 0)));
        assertTrue(board.isAvailable(new Position(0, 0)));
    }

    @Test
    void placesOnePlayerState() {
        Board board = new GridBoard(3, 3);
        Position position = new Position(1, 2);

        board.place(position, CellState.PLAYER_ONE);

        assertEquals(CellState.PLAYER_ONE, board.getCell(position));
        assertFalse(board.isAvailable(position));
    }

    @Test
    void oneByOneBoardBecomesFullAfterPlacement() {
        Board board = new GridBoard(1, 1);

        board.place(new Position(0, 0), CellState.PLAYER_TWO);

        assertTrue(board.isFull());
        assertTrue(board.getAvailablePositions().isEmpty());
    }

    // Many

    @Test
    void storesMultiplePlacementsIndependently() {
        Board board = new GridBoard(3, 3);

        board.place(new Position(0, 0), CellState.PLAYER_ONE);
        board.place(new Position(1, 1), CellState.PLAYER_TWO);
        board.place(new Position(2, 2), CellState.PLAYER_ONE);

        assertEquals(CellState.PLAYER_ONE, board.getCell(new Position(0, 0)));
        assertEquals(CellState.PLAYER_TWO, board.getCell(new Position(1, 1)));
        assertEquals(CellState.PLAYER_ONE, board.getCell(new Position(2, 2)));
        assertEquals(CellState.EMPTY, board.getCell(new Position(0, 1)));
    }

    @Test
    void availablePositionsExcludeOccupiedCells() {
        Board board = new GridBoard(2, 2);

        board.place(new Position(0, 1), CellState.PLAYER_ONE);
        board.place(new Position(1, 0), CellState.PLAYER_TWO);

        assertEquals(List.of(new Position(0, 0), new Position(1, 1)), board.getAvailablePositions());
    }

    @Test
    void supportsRectangularBoards() {
        Board board = new GridBoard(2, 4);

        assertEquals(2, board.getRowCount());
        assertEquals(4, board.getColumnCount());
        assertEquals(8, board.getAvailablePositions().size());
        assertTrue(board.isWithinBounds(new Position(1, 3)));
    }

    // Boundary

    @Test
    void cornerPositionsAreWithinBounds() {
        Board board = new GridBoard(3, 3);

        assertTrue(board.isWithinBounds(new Position(0, 0)));
        assertTrue(board.isWithinBounds(new Position(0, 2)));
        assertTrue(board.isWithinBounds(new Position(2, 0)));
        assertTrue(board.isWithinBounds(new Position(2, 2)));
    }

    @Test
    void positionsImmediatelyOutsideBoardAreNotWithinBounds() {
        Board board = new GridBoard(3, 3);

        assertFalse(board.isWithinBounds(new Position(-1, 0)));
        assertFalse(board.isWithinBounds(new Position(0, -1)));
        assertFalse(board.isWithinBounds(new Position(3, 0)));
        assertFalse(board.isWithinBounds(new Position(0, 3)));
    }

    @Test
    void positionsOutsideBoardAreNotAvailable() {
        Board board = new GridBoard(3, 3);

        assertFalse(board.isAvailable(new Position(-1, 0)));
        assertFalse(board.isAvailable(new Position(3, 3)));
    }

    @Test
    void rejectsBoardLargerThanMaximumCellCount() {
        assertThrows(IllegalArgumentException.class, () -> new GridBoard(1_001, 1_000));
    }

    @Test
    void rejectsHugeDimensionsWithoutIntegerOverflow() {
        assertThrows(IllegalArgumentException.class, () -> new GridBoard(Integer.MAX_VALUE, Integer.MAX_VALUE));
    }

    // Interface

    @Test
    void availablePositionListIsImmutable() {
        Board board = new GridBoard(2, 2);
        List<Position> positions = board.getAvailablePositions();

        assertThrows(UnsupportedOperationException.class, () -> positions.add(new Position(0, 0)));
    }

    @Test
    void copiedBoardContainsTheSameCellStates() {
        Board original = new GridBoard(3, 3);
        original.place(new Position(0, 0), CellState.PLAYER_ONE);
        original.place(new Position(1, 1), CellState.PLAYER_TWO);

        Board copy = original.copy();

        assertNotSame(original, copy);
        assertEquals(CellState.PLAYER_ONE, copy.getCell(new Position(0, 0)));
        assertEquals(CellState.PLAYER_TWO, copy.getCell(new Position(1, 1)));
        assertEquals(CellState.EMPTY, copy.getCell(new Position(2, 2)));
    }

    @Test
    void copiedBoardCanChangeWithoutChangingOriginal() {
        Board original = new GridBoard(3, 3);
        original.place(new Position(0, 0), CellState.PLAYER_ONE);

        Board copy = original.copy();
        copy.place(new Position(1, 1), CellState.PLAYER_TWO);

        assertEquals(CellState.EMPTY, original.getCell(new Position(1, 1)));
        assertEquals(CellState.PLAYER_TWO, copy.getCell(new Position(1, 1)));
    }

    // Exception

    @Test
    void negativeDimensionsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new GridBoard(-1, 3));
        assertThrows(IllegalArgumentException.class, () -> new GridBoard(3, -1));
    }

    @Test
    void getCellRejectsOutOfBoundsPosition() {
        Board board = new GridBoard(3, 3);

        assertThrows(IndexOutOfBoundsException.class, () -> board.getCell(new Position(3, 0)));
    }

    @Test
    void placeRejectsOutOfBoundsPosition() {
        Board board = new GridBoard(3, 3);

        assertThrows(IndexOutOfBoundsException.class, () -> board.place(new Position(-1, 0), CellState.PLAYER_ONE));
    }

    @Test
    void methodsRejectNullPositions() {
        Board board = new GridBoard(3, 3);

        assertThrows(NullPointerException.class, () -> board.isWithinBounds(null));
        assertThrows(NullPointerException.class, () -> board.getCell(null));
        assertThrows(NullPointerException.class, () -> board.isAvailable(null));
        assertThrows(NullPointerException.class, () -> board.place(null, CellState.PLAYER_ONE));
    }

    @Test
    void placeRejectsNullCellState() {
        Board board = new GridBoard(3, 3);

        assertThrows(NullPointerException.class, () -> board.place(new Position(0, 0), null));
    }

    @Test
    void placeRejectsEmptyCellState() {
        Board board = new GridBoard(3, 3);

        assertThrows(IllegalArgumentException.class, () -> board.place(new Position(0, 0), CellState.EMPTY));
    }

    @Test
    void occupiedPositionCannotBeOverwritten() {
        Board board = new GridBoard(3, 3);
        Position position = new Position(0, 0);
        board.place(position, CellState.PLAYER_ONE);

        assertThrows(IllegalStateException.class, () -> board.place(position, CellState.PLAYER_TWO));
        assertEquals(CellState.PLAYER_ONE, board.getCell(position));
    }
}
