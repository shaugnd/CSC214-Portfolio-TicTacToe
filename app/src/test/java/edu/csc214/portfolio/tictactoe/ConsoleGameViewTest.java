package edu.csc214.portfolio.tictactoe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.StringWriter;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

/**
 * Tests console messages, injected formatting and symbols, outcome
 * presentation, and defensive validation in {@link ConsoleGameView}.
 *
 * <p>The test cases follow the ZOMBIE method: Zero, One, Many, Boundary,
 * Interface, and Exception.</p>
 */
class ConsoleGameViewTest {
    private static final String LINE = System.lineSeparator();

    // Zero

    @Test
    void placedMoveProducesNoMessage() {
        StringWriter output = new StringWriter();
        GameView view = createView(output);

        view.displayMoveResult(turnResult(MoveStatus.PLACED));

        assertEquals("", output.toString());
    }

    // One

    @Test
    void displaysWelcomeMessage() {
        StringWriter output = new StringWriter();
        GameView view = createView(output);

        view.displayWelcome();

        String expected = "Welcome to Tic-Tac-Toe!" + LINE
                + "Choose a numbered cell to place your mark." + LINE;

        assertEquals(expected, output.toString());
    }

    @Test
    void displaysBoardUsingInjectedFormatter() {
        StringWriter output = new StringWriter();
        BoardFormatter formatter = ignoredBoard -> "FORMATTED BOARD";
        GameView view = new ConsoleGameView(output, formatter, new ClassicCellSymbolProvider());

        view.displayBoard(new GridBoard(3, 3));

        assertEquals(LINE + "FORMATTED BOARD" + LINE + LINE, output.toString());
    }

    @Test
    void passesSameBoardToFormatter() {
        StringWriter output = new StringWriter();
        AtomicReference<Board> receivedBoard = new AtomicReference<>();
        BoardFormatter formatter = board -> {
            receivedBoard.set(board);
            return "board";
        };
        GameView view = new ConsoleGameView(output, formatter, new ClassicCellSymbolProvider());
        Board board = new GridBoard(3, 3);

        view.displayBoard(board);

        assertSame(board, receivedBoard.get());
    }

    @Test
    void displaysPlayerTurnWithClassicSymbol() {
        StringWriter output = new StringWriter();
        GameView view = createView(output);

        view.displayTurn(player("Alex", CellState.PLAYER_ONE));

        assertEquals("Alex's turn (X)." + LINE, output.toString());
    }

    @Test
    void displaysPlayerTwoTurnWithClassicSymbol() {
        StringWriter output = new StringWriter();
        GameView view = createView(output);

        view.displayTurn(player("Jordan", CellState.PLAYER_TWO));

        assertEquals("Jordan's turn (O)." + LINE, output.toString());
    }

    @Test
    void usesInjectedSymbolProviderForTurnDisplay() {
        StringWriter output = new StringWriter();
        CellSymbolProvider symbols = state -> switch (state) {
            case EMPTY -> ".";
            case PLAYER_ONE -> "ONE";
            case PLAYER_TWO -> "TWO";
        };
        GameView view = new ConsoleGameView(output, ignoredBoard -> "board", symbols);

        view.displayTurn(player("Alex", CellState.PLAYER_ONE));

        assertEquals("Alex's turn (ONE)." + LINE, output.toString());
    }

    // Many

    @Test
    void displaysOccupiedCellMessage() {
        StringWriter output = new StringWriter();
        GameView view = createView(output);

        view.displayMoveResult(turnResult(MoveStatus.OCCUPIED));

        assertEquals("That cell is already occupied. Try again." + LINE, output.toString());
    }

    @Test
    void displaysOutOfBoundsMessage() {
        StringWriter output = new StringWriter();
        GameView view = createView(output);

        view.displayMoveResult(turnResult(MoveStatus.OUT_OF_BOUNDS));

        assertEquals("That cell is outside the board. Try again." + LINE, output.toString());
    }

    @Test
    void displaysGameAlreadyOverMessage() {
        StringWriter output = new StringWriter();
        GameView view = createView(output);

        view.displayMoveResult(turnResult(MoveStatus.GAME_ALREADY_OVER));

        assertEquals("The game is already over." + LINE, output.toString());
    }

    @Test
    void displaysPlayerOneVictory() {
        StringWriter output = new StringWriter();
        GameView view = createView(output);

        view.displayOutcome(GameOutcome.PLAYER_ONE_WINS, player("Alex", CellState.PLAYER_ONE), player("Jordan", CellState.PLAYER_TWO));

        assertEquals("Alex wins!" + LINE, output.toString());
    }

    @Test
    void displaysPlayerTwoVictory() {
        StringWriter output = new StringWriter();
        GameView view = createView(output);

        view.displayOutcome(GameOutcome.PLAYER_TWO_WINS, player("Alex", CellState.PLAYER_ONE), player("Jordan", CellState.PLAYER_TWO));

        assertEquals("Jordan wins!" + LINE, output.toString());
    }

    @Test
    void displaysDrawOutcome() {
        StringWriter output = new StringWriter();
        GameView view = createView(output);

        view.displayOutcome(GameOutcome.DRAW, player("Alex", CellState.PLAYER_ONE), player("Jordan", CellState.PLAYER_TWO));

        assertEquals("The game ends in a draw." + LINE, output.toString());
    }

    @Test
    void displaysInProgressOutcome() {
        StringWriter output = new StringWriter();
        GameView view = createView(output);

        view.displayOutcome(GameOutcome.IN_PROGRESS, player("Alex", CellState.PLAYER_ONE), player("Jordan", CellState.PLAYER_TWO));

        assertEquals("The game is still in progress." + LINE, output.toString());
    }

    // Interface

    @Test
    void worksThroughGameViewInterface() {
        StringWriter output = new StringWriter();
        GameView view = createView(output);

        view.displayWelcome();

        assertEquals("Welcome to Tic-Tac-Toe!" + LINE
                + "Choose a numbered cell to place your mark." + LINE, output.toString());
    }

    // Exception

    @Test
    void constructorRejectsNullDependencies() {
        assertThrows(NullPointerException.class, () -> new ConsoleGameView(null, ignoredBoard -> "board", new ClassicCellSymbolProvider()));
        assertThrows(NullPointerException.class, () -> new ConsoleGameView(new StringWriter(), null, new ClassicCellSymbolProvider()));
        assertThrows(NullPointerException.class, () -> new ConsoleGameView(new StringWriter(), ignoredBoard -> "board", null));
    }

    @Test
    void displayBoardRejectsNullBoard() {
        GameView view = createView(new StringWriter());

        assertThrows(NullPointerException.class, () -> view.displayBoard(null));
    }

    @Test
    void displayTurnRejectsNullPlayer() {
        GameView view = createView(new StringWriter());

        assertThrows(NullPointerException.class, () -> view.displayTurn(null));
    }

    @Test
    void displayMoveResultRejectsNullResult() {
        GameView view = createView(new StringWriter());

        assertThrows(NullPointerException.class, () -> view.displayMoveResult(null));
    }

    @Test
    void displayOutcomeRejectsNullArguments() {
        GameView view = createView(new StringWriter());
        Player firstPlayer = player("Alex", CellState.PLAYER_ONE);
        Player secondPlayer = player("Jordan", CellState.PLAYER_TWO);

        assertThrows(NullPointerException.class, () -> view.displayOutcome(null, firstPlayer, secondPlayer));
        assertThrows(NullPointerException.class, () -> view.displayOutcome(GameOutcome.DRAW, null, secondPlayer));
        assertThrows(NullPointerException.class, () -> view.displayOutcome(GameOutcome.DRAW, firstPlayer, null));
    }

    private static GameView createView(StringWriter output) {
        return new ConsoleGameView(output, ignoredBoard -> "board", new ClassicCellSymbolProvider());
    }

    private static TurnResult turnResult(MoveStatus status) {
        Player player = player("Alex", CellState.PLAYER_ONE);
        MoveResolution resolution = new MoveResolution(status, status == MoveStatus.PLACED);
        return new TurnResult(player, new Position(0, 0), resolution, GameOutcome.IN_PROGRESS);
    }

    private static Player player(String name, CellState state) {
        return new HumanPlayer(name, state, (ignoredPlayer, ignoredBoard) -> new Position(0, 0));
    }
}
