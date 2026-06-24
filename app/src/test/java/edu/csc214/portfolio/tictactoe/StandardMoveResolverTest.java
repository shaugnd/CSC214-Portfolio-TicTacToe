package edu.csc214.portfolio.tictactoe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests ordinary move placement, invalid move handling, turn consumption, and
 * defensive validation in {@link StandardMoveResolver}.
 *
 * <p>The test cases follow the ZOMBIE method: Zero, One, Many, Boundary,
 * Interface, and Exception.</p>
 */
class StandardMoveResolverTest {
    private final MoveResolver resolver = new StandardMoveResolver();

    // Zero

    @Test
    void outOfBoundsMovePlacesZeroMarks() {
        Board board = new GridBoard(3, 3);

        MoveResolution resolution = resolver.resolve(board, new Position(-1, 0), CellState.PLAYER_ONE);
        assertEquals(MoveStatus.OUT_OF_BOUNDS, resolution.status());
        assertFalse(resolution.turnConsumed());
        assertEquals(9, board.getAvailablePositions().size());
    }

    @Test
    void occupiedMovePlacesZeroAdditionalMarks() {
        Board board = new GridBoard(3, 3);
        Position position = new Position(1, 1);
        board.place(position, CellState.PLAYER_ONE);

        MoveResolution resolution = resolver.resolve(board, position, CellState.PLAYER_TWO);
        assertEquals(MoveStatus.OCCUPIED, resolution.status());
        assertFalse(resolution.turnConsumed());
        assertEquals(CellState.PLAYER_ONE, board.getCell(position));
        assertEquals(8, board.getAvailablePositions().size());
    }

    // One

    @Test
    void placesOnePlayerOneMark() {
        Board board = new GridBoard(3, 3);
        Position position = new Position(0, 2);
        MoveResolution resolution = resolver.resolve(board, position, CellState.PLAYER_ONE);
        assertEquals(MoveStatus.PLACED, resolution.status());
        assertEquals(CellState.PLAYER_ONE, board.getCell(position));
    }

    @Test
    void placesOnePlayerTwoMark() {
        Board board = new GridBoard(3, 3);
        Position position = new Position(2, 0);
        MoveResolution resolution = resolver.resolve(board, position, CellState.PLAYER_TWO);
        assertEquals(MoveStatus.PLACED, resolution.status());
        assertEquals(CellState.PLAYER_TWO, board.getCell(position));
    }

    @Test
    void successfulMoveConsumesTurn() {
        Board board = new GridBoard(3, 3);
        MoveResolution resolution = resolver.resolve(board, new Position(0, 0), CellState.PLAYER_ONE);
        assertTrue(resolution.turnConsumed());
    }

    // Many

    @Test
    void resolvesMultipleValidMovesIndependently() {
        Board board = new GridBoard(3, 3);
        MoveResolution first = resolver.resolve(board, new Position(0, 0), CellState.PLAYER_ONE);
        MoveResolution second = resolver.resolve(board, new Position(1, 1), CellState.PLAYER_TWO);
        MoveResolution third = resolver.resolve(board, new Position(2, 2), CellState.PLAYER_ONE);
        assertEquals(MoveStatus.PLACED, first.status());
        assertEquals(MoveStatus.PLACED, second.status());
        assertEquals(MoveStatus.PLACED, third.status());
        assertEquals(6, board.getAvailablePositions().size());
    }

    @Test
    void failedMoveDoesNotPreventLaterValidMove() {
        Board board = new GridBoard(3, 3);
        MoveResolution failed = resolver.resolve(board, new Position(3, 0), CellState.PLAYER_ONE);
        MoveResolution successful = resolver.resolve(board, new Position(2, 0), CellState.PLAYER_ONE);
        assertEquals(MoveStatus.OUT_OF_BOUNDS, failed.status());
        assertFalse(failed.turnConsumed());
        assertEquals(MoveStatus.PLACED, successful.status());
        assertTrue(successful.turnConsumed());
    }

    // Boundary

    @Test
    void allCornerPositionsCanBeResolved() {
        Board board = new GridBoard(3, 3);
        assertEquals(MoveStatus.PLACED, resolver.resolve(board, new Position(0, 0), CellState.PLAYER_ONE).status());
        assertEquals(MoveStatus.PLACED, resolver.resolve(board, new Position(0, 2), CellState.PLAYER_TWO).status());
        assertEquals(MoveStatus.PLACED, resolver.resolve(board, new Position(2, 0), CellState.PLAYER_ONE).status());
        assertEquals(MoveStatus.PLACED, resolver.resolve(board, new Position(2, 2), CellState.PLAYER_TWO).status());
    }

    @Test
    void positionsImmediatelyBeyondEachBoundaryAreRejected() {
        Board board = new GridBoard(3, 3);

        assertEquals(MoveStatus.OUT_OF_BOUNDS, resolver.resolve(board, new Position(-1, 0), CellState.PLAYER_ONE).status());
        assertEquals(MoveStatus.OUT_OF_BOUNDS, resolver.resolve(board, new Position(0, -1), CellState.PLAYER_ONE).status());
        assertEquals(MoveStatus.OUT_OF_BOUNDS, resolver.resolve(board, new Position(3, 0), CellState.PLAYER_ONE).status());
        assertEquals(MoveStatus.OUT_OF_BOUNDS, resolver.resolve(board, new Position(0, 3), CellState.PLAYER_ONE).status());
    }

    // Interface

    @Test
    void resolvesMoveThroughBoardAndResolverInterfaces() {
        Board board = new GridBoard(3, 3);
        MoveResolver moveResolver = new StandardMoveResolver();

        MoveResolution resolution = moveResolver.resolve(board, new Position(1, 2), CellState.PLAYER_TWO);

        assertEquals(MoveStatus.PLACED, resolution.status());
        assertTrue(resolution.turnConsumed());
        assertEquals(CellState.PLAYER_TWO, board.getCell(new Position(1, 2)));
    }

    // Exception

    @Test
    void rejectsNullBoard() {
        assertThrows(NullPointerException.class, () -> resolver.resolve(null, new Position(0, 0), CellState.PLAYER_ONE));
    }

    @Test
    void rejectsNullPosition() {
        Board board = new GridBoard(3, 3);
        assertThrows(NullPointerException.class, () -> resolver.resolve(board, null, CellState.PLAYER_ONE));
    }

    @Test
    void rejectsNullCellState() {
        Board board = new GridBoard(3, 3);
        assertThrows(NullPointerException.class, () -> resolver.resolve(board, new Position(0, 0), null));
    }

    @Test
    void rejectsEmptyCellState() {
        Board board = new GridBoard(3, 3);
        assertThrows(IllegalArgumentException.class, () -> resolver.resolve(board, new Position(0, 0), CellState.EMPTY));
    }
}
