package edu.csc214.portfolio.tictactoe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import org.junit.jupiter.api.Test;

/**
 * Tests valid move input, retry behavior, position mapping, prompts, and
 * defensive error handling in {@link ConsoleMoveSource}.
 *
 * <p>The test cases follow the ZOMBIE method: Zero, One, Many, Boundary,
 * Interface, and Exception.</p>
 */
class ConsoleMoveSourceTest {

    // Zero

    @Test
    void zeroIsRejectedBeforeValidCellIsAccepted() {
        StringWriter output = new StringWriter();
        MoveSource moveSource = sourceWith("0\n1\n", output);

        Position position = moveSource.requestMove(player("Alex"), new GridBoard(3, 3));

        assertEquals(new Position(0, 0), position);
        assertTrue(output.toString().contains("Please enter a whole number between 1 and 9."));
    }

    // One

    @Test
    void firstCellMapsToUpperLeftPosition() {
        MoveSource moveSource = sourceWith("1\n", new StringWriter());

        Position position = moveSource.requestMove(player("Alex"), new GridBoard(3, 3));

        assertEquals(new Position(0, 0), position);
    }

    @Test
    void acceptsOnlyCellOnOneByOneBoard() {
        MoveSource moveSource = sourceWith("1\n", new StringWriter());

        Position position = moveSource.requestMove(player("Alex"), new GridBoard(1, 1));

        assertEquals(new Position(0, 0), position);
    }

    @Test
    void trimsWhitespaceAroundCellNumber() {
        MoveSource moveSource = sourceWith("   5   \n", new StringWriter());

        Position position = moveSource.requestMove(player("Alex"), new GridBoard(3, 3));

        assertEquals(new Position(1, 1), position);
    }

    // Many

    @Test
    void retriesAfterSeveralInvalidFormats() {
        StringWriter output = new StringWriter();
        MoveSource moveSource = sourceWith("banana\n3.5\n\n999999999999999999999\n5\n", output);

        Position position = moveSource.requestMove(player("Alex"), new GridBoard(3, 3));

        assertEquals(new Position(1, 1), position);
        assertEquals(4, countOccurrences(output.toString(), "Please enter a whole number between 1 and 9."));
    }

    @Test
    void retriesAfterCellNumbersBelowAndAboveRange() {
        StringWriter output = new StringWriter();
        MoveSource moveSource = sourceWith("-1\n10\n9\n", output);

        Position position = moveSource.requestMove(player("Alex"), new GridBoard(3, 3));

        assertEquals(new Position(2, 2), position);
        assertEquals(2, countOccurrences(output.toString(), "Please enter a whole number between 1 and 9."));
    }

    // Boundary

    @Test
    void lastClassicCellMapsToLowerRightPosition() {
        MoveSource moveSource = sourceWith("9\n", new StringWriter());

        Position position = moveSource.requestMove(player("Alex"), new GridBoard(3, 3));

        assertEquals(new Position(2, 2), position);
    }

    @Test
    void lastRectangularCellMapsCorrectly() {
        MoveSource moveSource = sourceWith("8\n", new StringWriter());

        Position position = moveSource.requestMove(player("Alex"), new GridBoard(2, 4));

        assertEquals(new Position(1, 3), position);
    }

    @Test
    void occupiedCellCanStillBeSelectedByInputLayer() {
        Board board = new GridBoard(3, 3);
        board.place(new Position(0, 0), CellState.PLAYER_ONE);
        MoveSource moveSource = sourceWith("1\n", new StringWriter());

        Position position = moveSource.requestMove(player("Alex"), board);

        assertEquals(new Position(0, 0), position);
    }

    // Interface

    @Test
    void worksThroughMoveSourceInterface() {
        MoveSource moveSource = sourceWith("8\n", new StringWriter());

        assertEquals(new Position(2, 1), moveSource.requestMove(player("Alex"), new GridBoard(3, 3)));
    }

    @Test
    void promptIncludesPlayerNameAndCellRange() {
        StringWriter output = new StringWriter();
        MoveSource moveSource = sourceWith("5\n", output);

        moveSource.requestMove(player("Alex"), new GridBoard(3, 3));

        assertTrue(output.toString().contains("Alex, choose a cell (1-9):"));
    }

    // Exception

    @Test
    void constructorRejectsNullDependencies() {
        assertThrows(NullPointerException.class, () -> new ConsoleMoveSource(null, new StringWriter(), new RowMajorPositionMapper()));
        assertThrows(NullPointerException.class, () -> new ConsoleMoveSource(new StringReader(""), null, new RowMajorPositionMapper()));
        assertThrows(NullPointerException.class, () -> new ConsoleMoveSource(new StringReader(""), new StringWriter(), null));
    }

    @Test
    void requestMoveRejectsNullPlayer() {
        MoveSource moveSource = sourceWith("1\n", new StringWriter());

        assertThrows(NullPointerException.class, () -> moveSource.requestMove(null, new GridBoard(3, 3)));
    }

    @Test
    void requestMoveRejectsNullBoard() {
        MoveSource moveSource = sourceWith("1\n", new StringWriter());

        assertThrows(NullPointerException.class, () -> moveSource.requestMove(player("Alex"), null));
    }

    @Test
    void endOfInputBeforeMoveThrowsException() {
        MoveSource moveSource = sourceWith("", new StringWriter());

        assertThrows(IllegalStateException.class, () -> moveSource.requestMove(player("Alex"), new GridBoard(3, 3)));
    }

    @Test
    void readerFailureThrowsException() {
        Reader brokenReader = new Reader() {
            @Override
            public int read(char[] buffer, int offset, int length) throws IOException {
                throw new IOException("Simulated read failure.");
            }

            @Override
            public void close() {
            }
        };

        MoveSource moveSource = new ConsoleMoveSource(brokenReader, new StringWriter(), new RowMajorPositionMapper());

        assertThrows(IllegalStateException.class, () -> moveSource.requestMove(player("Alex"), new GridBoard(3, 3)));
    }

    private static MoveSource sourceWith(String input, StringWriter output) {
        return new ConsoleMoveSource(new StringReader(input), output, new RowMajorPositionMapper());
    }

    private static Player player(String name) {
        return new HumanPlayer(name, CellState.PLAYER_ONE, (ignoredPlayer, ignoredBoard) -> new Position(0, 0));
    }

    private static int countOccurrences(String text, String target) {
        int count = 0;
        int position = 0;

        while ((position = text.indexOf(target, position)) >= 0) {
            count++;
            position += target.length();
        }

        return count;
    }
}
