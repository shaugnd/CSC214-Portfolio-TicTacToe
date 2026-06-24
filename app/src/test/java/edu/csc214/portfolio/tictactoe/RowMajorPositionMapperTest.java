package edu.csc214.portfolio.tictactoe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Tests conversion between user-facing cell numbers and board positions.
 *
 * <p>The test cases follow the ZOMBIE method: Zero, One, Many, Boundary,
 * Interface, and Exception.</p>
 */
class RowMajorPositionMapperTest {
    private final PositionMapper mapper = new RowMajorPositionMapper();

    // Zero

    @Test
    void zeroCellNumberIsRejected() {
        Board board = new GridBoard(3, 3);

        assertThrows(IndexOutOfBoundsException.class, () -> mapper.toPosition(0, board));
    }

    // One

    @Test
    void firstCellMapsToUpperLeftPosition() {
        Board board = new GridBoard(3, 3);

        assertEquals(new Position(0, 0), mapper.toPosition(1, board));
    }

    @Test
    void upperLeftPositionMapsToFirstCell() {
        Board board = new GridBoard(3, 3);

        assertEquals(1, mapper.toCellNumber(new Position(0, 0), board));
    }

    @Test
    void oneByOneBoardMapsItsOnlyCell() {
        Board board = new GridBoard(1, 1);

        assertEquals(new Position(0, 0), mapper.toPosition(1, board));
        assertEquals(1, mapper.toCellNumber(new Position(0, 0), board));
    }

    // Many

    @Test
    void mapsEveryCellOnClassicBoard() {
        Board board = new GridBoard(3, 3);

        for (int cellNumber = 1; cellNumber <= 9; cellNumber++) {
            Position position = mapper.toPosition(cellNumber, board);
            assertEquals(cellNumber, mapper.toCellNumber(position, board));
        }
    }

    @Test
    void mapsRectangularBoardInRowMajorOrder() {
        Board board = new GridBoard(2, 4);

        assertEquals(new Position(0, 0), mapper.toPosition(1, board));
        assertEquals(new Position(0, 3), mapper.toPosition(4, board));
        assertEquals(new Position(1, 0), mapper.toPosition(5, board));
        assertEquals(new Position(1, 3), mapper.toPosition(8, board));
    }

    @Test
    void mapsMiddleCellOnClassicBoard() {
        Board board = new GridBoard(3, 3);

        assertEquals(new Position(1, 1), mapper.toPosition(5, board));
        assertEquals(5, mapper.toCellNumber(new Position(1, 1), board));
    }

    // Boundary

    @Test
    void lastCellMapsToLowerRightPosition() {
        Board board = new GridBoard(3, 3);

        assertEquals(new Position(2, 2), mapper.toPosition(9, board));
        assertEquals(9, mapper.toCellNumber(new Position(2, 2), board));
    }

    @Test
    void cellNumberImmediatelyAboveRangeIsRejected() {
        Board board = new GridBoard(3, 3);

        assertThrows(IndexOutOfBoundsException.class, () -> mapper.toPosition(10, board));
    }

    @Test
    void negativeCellNumberIsRejected() {
        Board board = new GridBoard(3, 3);

        assertThrows(IndexOutOfBoundsException.class, () -> mapper.toPosition(-1, board));
    }

    @Test
    void positionImmediatelyOutsideEachBoundaryIsRejected() {
        Board board = new GridBoard(3, 3);

        assertThrows(IndexOutOfBoundsException.class, () -> mapper.toCellNumber(new Position(-1, 0), board));
        assertThrows(IndexOutOfBoundsException.class, () -> mapper.toCellNumber(new Position(0, -1), board));
        assertThrows(IndexOutOfBoundsException.class, () -> mapper.toCellNumber(new Position(3, 0), board));
        assertThrows(IndexOutOfBoundsException.class, () -> mapper.toCellNumber(new Position(0, 3), board));
    }

    // Interface

    @Test
    void mapperWorksThroughBoardAndPositionMapperInterfaces() {
        Board board = new GridBoard(3, 3);
        PositionMapper positionMapper = new RowMajorPositionMapper();

        assertEquals(new Position(2, 1), positionMapper.toPosition(8, board));
        assertEquals(8, positionMapper.toCellNumber(new Position(2, 1), board));
    }

    // Exception

    @Test
    void toPositionRejectsNullBoard() {
        assertThrows(NullPointerException.class, () -> mapper.toPosition(1, null));
    }

    @Test
    void toCellNumberRejectsNullBoard() {
        assertThrows(NullPointerException.class, () -> mapper.toCellNumber(new Position(0, 0), null));
    }

    @Test
    void toCellNumberRejectsNullPosition() {
        Board board = new GridBoard(3, 3);

        assertThrows(NullPointerException.class, () -> mapper.toCellNumber(null, board));
    }
}
