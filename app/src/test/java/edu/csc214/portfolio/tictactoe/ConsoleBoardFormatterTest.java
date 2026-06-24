package edu.csc214.portfolio.tictactoe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests text-board rendering, symbol substitution, alignment, rectangular
 * layouts, and defensive validation in {@link ConsoleBoardFormatter}.
 *
 * <p>The test cases follow the ZOMBIE method: Zero, One, Many, Boundary,
 * Interface, and Exception.</p>
 */
class ConsoleBoardFormatterTest {
    // I'm not testing every single possibility.  That would be a massive test suite.
    // I've carved out representative sample states that fit every categorical situation
    // that I could come pu with.
    
    private final BoardFormatter formatter = new ConsoleBoardFormatter(new RowMajorPositionMapper(), new ClassicCellSymbolProvider());

    // Zero

    @Test
    void formatsEmptyClassicBoard() {
        String line = System.lineSeparator();
        String expected = "1 | 2 | 3" + line
                + "--+---+--" + line
                + "4 | 5 | 6" + line
                + "--+---+--" + line
                + "7 | 8 | 9";

        assertEquals(expected, formatter.format(new GridBoard(3, 3)));
    }

    // One

    @Test
    void formatsOnePlayerOneMarkAsX() {
        Board board = new GridBoard(3, 3);
        board.place(new Position(0, 0), CellState.PLAYER_ONE);

        String output = formatter.format(board);

        assertEquals("X | 2 | 3", output.lines().findFirst().orElseThrow());
    }

    @Test
    void formatsOnePlayerTwoMarkAsO() {
        Board board = new GridBoard(3, 3);
        board.place(new Position(1, 1), CellState.PLAYER_TWO);

        String output = formatter.format(board);

        assertEquals("4 | O | 6", output.lines().skip(2).findFirst().orElseThrow());
    }

    // Many

    @Test
    void formatsMixedClassicBoard() {
        String line = System.lineSeparator();
        Board board = new GridBoard(3, 3);
        board.place(new Position(0, 0), CellState.PLAYER_ONE);
        board.place(new Position(0, 2), CellState.PLAYER_TWO);
        board.place(new Position(1, 1), CellState.PLAYER_ONE);
        board.place(new Position(2, 0), CellState.PLAYER_TWO);

        String expected = "X | 2 | O" + line
                + "--+---+--" + line
                + "4 | X | 6" + line
                + "--+---+--" + line
                + "O | 8 | 9";

        assertEquals(expected, formatter.format(board));
    }

    @Test
    void formatsRectangularBoard() {
        String line = System.lineSeparator();
        Board board = new GridBoard(2, 4);
        String expected = "1 | 2 | 3 | 4" + line
                + "--+---+---+--" + line
                + "5 | 6 | 7 | 8";

        assertEquals(expected, formatter.format(board));
    }

    @Test
    void usesInjectedSymbolProvider() {
        Board board = new GridBoard(1, 2);
        board.place(new Position(0, 0), CellState.PLAYER_ONE);
        CellSymbolProvider symbols = state -> switch (state) {
            case EMPTY -> ".";
            case PLAYER_ONE -> "ONE";
            case PLAYER_TWO -> "TWO";
        };
        BoardFormatter customFormatter = new ConsoleBoardFormatter(new RowMajorPositionMapper(), symbols);

        assertEquals("ONE |   2", customFormatter.format(board));
    }

    // Boundary

    @Test
    void alignsBoardsWithTwoDigitCellNumbers() {
        String line = System.lineSeparator();
        Board board = new GridBoard(4, 3);
        String expectedLastRows = " 7 |  8 |  9" + line
                + "---+----+---" + line
                + "10 | 11 | 12";

        String output = formatter.format(board);

        assertEquals(expectedLastRows, output.lines().skip(4).reduce((first, second) -> first + line + second).orElseThrow());
    }

    @Test
    void formatsOneByOneBoard() {
        Board board = new GridBoard(1, 1);

        assertEquals("1", formatter.format(board));

        board.place(new Position(0, 0), CellState.PLAYER_ONE);

        assertEquals("X", formatter.format(board));
    }

    // Interface

    @Test
    void formatterWorksThroughInterfaces() {
        Board board = new GridBoard(3, 3);
        BoardFormatter boardFormatter = new ConsoleBoardFormatter(new RowMajorPositionMapper(), new ClassicCellSymbolProvider());

        assertEquals(formatter.format(board), boardFormatter.format(board));
    }

    @Test
    void formattingDoesNotModifyBoard() {
        Board board = new GridBoard(3, 3);
        board.place(new Position(0, 0), CellState.PLAYER_ONE);
        List<Position> availableBefore = board.getAvailablePositions();

        formatter.format(board);

        assertEquals(CellState.PLAYER_ONE, board.getCell(new Position(0, 0)));
        assertEquals(availableBefore, board.getAvailablePositions());
    }

    // Exception

    @Test
    void constructorRejectsNullPositionMapper() {
        assertThrows(NullPointerException.class, () -> new ConsoleBoardFormatter(null, new ClassicCellSymbolProvider()));
    }

    @Test
    void constructorRejectsNullSymbolProvider() {
        assertThrows(NullPointerException.class, () -> new ConsoleBoardFormatter(new RowMajorPositionMapper(), null));
    }

    @Test
    void formatRejectsNullBoard() {
        assertThrows(NullPointerException.class, () -> formatter.format(null));
    }
}
