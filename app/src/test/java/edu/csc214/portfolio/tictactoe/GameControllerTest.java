package edu.csc214.portfolio.tictactoe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests complete application workflows coordinated by {@link GameController}.
 *
 * <p>The tests verify terminal starting states, normal alternation, invalid
 * retries, victories, draws, display timing, and isolation of engine state from
 * board snapshots supplied to players.</p>
 */
class GameControllerTest {

    // Zero

    @Test
    void completedGameSkipsAllTurnsAndDisplaysExistingOutcome() {
        Board board = new GridBoard(3, 3);
        board.place(new Position(0, 0), CellState.PLAYER_ONE);
        board.place(new Position(0, 1), CellState.PLAYER_ONE);
        board.place(new Position(0, 2), CellState.PLAYER_ONE);

        Player firstPlayer = unusedPlayer("Alex", CellState.PLAYER_ONE);
        Player secondPlayer = unusedPlayer("Jordan", CellState.PLAYER_TWO);
        GameEngine engine = createEngine(board, firstPlayer, secondPlayer);
        RecordingGameView view = new RecordingGameView();

        new GameController(engine, view, firstPlayer, secondPlayer).playGame();

        assertEquals(List.of("welcome", "board", "outcome:PLAYER_ONE_WINS"), view.events);
        assertTrue(view.displayedTurns.isEmpty());
        assertTrue(view.moveResults.isEmpty());
        assertEquals(1, view.displayedBoards.size());
    }

    // One

    @Test
    void oneMoveGameUsesExpectedControllerSequence() {
        Board board = new GridBoard(3, 3);
        board.place(new Position(0, 0), CellState.PLAYER_ONE);
        board.place(new Position(0, 1), CellState.PLAYER_ONE);
        board.place(new Position(1, 0), CellState.PLAYER_TWO);
        board.place(new Position(1, 1), CellState.PLAYER_TWO);

        Player firstPlayer = scriptedPlayer("Alex", CellState.PLAYER_ONE, 3);
        Player secondPlayer = unusedPlayer("Jordan", CellState.PLAYER_TWO);
        GameEngine engine = createEngine(board, firstPlayer, secondPlayer);
        RecordingGameView view = new RecordingGameView();

        new GameController(engine, view, firstPlayer, secondPlayer).playGame();

        assertEquals(List.of(
                "welcome",
                "board",
                "turn:Alex",
                "result:PLACED",
                "board",
                "outcome:PLAYER_ONE_WINS"), view.events);

        assertEquals(CellState.PLAYER_ONE, engine.getBoardSnapshot().getCell(new Position(0, 2)));
    }

    // Many

    @Test
    void completeGameAlternatesPlayersUntilPlayerOneWins() {
        Player firstPlayer = scriptedPlayer("Alex", CellState.PLAYER_ONE, 1, 2, 3);
        Player secondPlayer = scriptedPlayer("Jordan", CellState.PLAYER_TWO, 4, 5);
        GameEngine engine = createEngine(new GridBoard(3, 3), firstPlayer, secondPlayer);
        RecordingGameView view = new RecordingGameView();

        new GameController(engine, view, firstPlayer, secondPlayer).playGame();

        assertEquals(List.of("Alex", "Jordan", "Alex", "Jordan", "Alex"), namesOf(view.displayedTurns));
        assertEquals(List.of(MoveStatus.PLACED, MoveStatus.PLACED, MoveStatus.PLACED, MoveStatus.PLACED, MoveStatus.PLACED), statusesOf(view.moveResults));
        assertEquals(6, view.displayedBoards.size());
        assertEquals(GameOutcome.PLAYER_ONE_WINS, view.outcome);
    }

    @Test
    void occupiedMoveRepeatsSamePlayerAndDoesNotRedisplayBoard() {
        Player firstPlayer = scriptedPlayer("Alex", CellState.PLAYER_ONE, 1, 2, 3);
        Player secondPlayer = scriptedPlayer("Jordan", CellState.PLAYER_TWO, 1, 4, 5);
        GameEngine engine = createEngine(new GridBoard(3, 3), firstPlayer, secondPlayer);
        RecordingGameView view = new RecordingGameView();

        new GameController(engine, view, firstPlayer, secondPlayer).playGame();

        assertEquals(List.of("Alex", "Jordan", "Jordan", "Alex", "Jordan", "Alex"), namesOf(view.displayedTurns));
        assertEquals(List.of(
                MoveStatus.PLACED,
                MoveStatus.OCCUPIED,
                MoveStatus.PLACED,
                MoveStatus.PLACED,
                MoveStatus.PLACED,
                MoveStatus.PLACED), statusesOf(view.moveResults));

        assertEquals(6, view.displayedBoards.size());
        assertEquals(GameOutcome.PLAYER_ONE_WINS, view.outcome);
    }

    @Test
    void playerTwoCanWinCompleteGame() {
        Player firstPlayer = scriptedPlayer("Alex", CellState.PLAYER_ONE, 1, 2, 9);
        Player secondPlayer = scriptedPlayer("Jordan", CellState.PLAYER_TWO, 4, 5, 6);
        GameEngine engine = createEngine(new GridBoard(3, 3), firstPlayer, secondPlayer);
        RecordingGameView view = new RecordingGameView();

        new GameController(engine, view, firstPlayer, secondPlayer).playGame();

        assertEquals(GameOutcome.PLAYER_TWO_WINS, view.outcome);
        assertSame(secondPlayer, engine.getCurrentPlayer());
        assertEquals("outcome:PLAYER_TWO_WINS", view.events.getLast());
    }

    @Test
    void completeGameCanEndInDraw() {
        Player firstPlayer = scriptedPlayer("Alex", CellState.PLAYER_ONE, 1, 3, 4, 8, 9);
        Player secondPlayer = scriptedPlayer("Jordan", CellState.PLAYER_TWO, 2, 5, 6, 7);
        GameEngine engine = createEngine(new GridBoard(3, 3), firstPlayer, secondPlayer);
        RecordingGameView view = new RecordingGameView();

        new GameController(engine, view, firstPlayer, secondPlayer).playGame();

        assertEquals(GameOutcome.DRAW, view.outcome);
        assertEquals(9, view.moveResults.size());
        assertEquals(10, view.displayedBoards.size());
        assertTrue(engine.getBoardSnapshot().isFull());
    }

    // Boundary

    @Test
    void playerCannotModifyEngineThroughMoveSelectionSnapshot() {
        Board board = new GridBoard(3, 3);
        board.place(new Position(0, 0), CellState.PLAYER_ONE);
        board.place(new Position(0, 1), CellState.PLAYER_ONE);
        board.place(new Position(1, 0), CellState.PLAYER_TWO);
        board.place(new Position(1, 1), CellState.PLAYER_TWO);

        MoveSource modifyingSource = (player, snapshot) -> {
            assertEquals(CellState.PLAYER_ONE, snapshot.getCell(new Position(0, 0)));
            snapshot.place(new Position(2, 2), CellState.PLAYER_ONE);
            return new Position(0, 2);
        };

        Player firstPlayer = new HumanPlayer("Alex", CellState.PLAYER_ONE, modifyingSource);
        Player secondPlayer = unusedPlayer("Jordan", CellState.PLAYER_TWO);
        GameEngine engine = createEngine(board, firstPlayer, secondPlayer);
        RecordingGameView view = new RecordingGameView();

        new GameController(engine, view, firstPlayer, secondPlayer).playGame();

        Board finalBoard = engine.getBoardSnapshot();

        assertEquals(CellState.PLAYER_ONE, finalBoard.getCell(new Position(0, 2)));
        assertEquals(CellState.EMPTY, finalBoard.getCell(new Position(2, 2)));
        assertEquals(GameOutcome.PLAYER_ONE_WINS, engine.getGameOutcome());
    }

    // Interface

    @Test
    void controllerPassesConfiguredPlayersToOutcomeView() {
        Board board = new GridBoard(3, 3);
        board.place(new Position(0, 0), CellState.PLAYER_ONE);
        board.place(new Position(0, 1), CellState.PLAYER_ONE);
        board.place(new Position(0, 2), CellState.PLAYER_ONE);

        Player firstPlayer = unusedPlayer("Alex", CellState.PLAYER_ONE);
        Player secondPlayer = unusedPlayer("Jordan", CellState.PLAYER_TWO);
        GameEngine engine = createEngine(board, firstPlayer, secondPlayer);
        RecordingGameView view = new RecordingGameView();

        new GameController(engine, view, firstPlayer, secondPlayer).playGame();

        assertSame(firstPlayer, view.outcomeFirstPlayer);
        assertSame(secondPlayer, view.outcomeSecondPlayer);
    }

    // Exception

    @Test
    void constructorRejectsNullDependencies() {
        Player firstPlayer = unusedPlayer("Alex", CellState.PLAYER_ONE);
        Player secondPlayer = unusedPlayer("Jordan", CellState.PLAYER_TWO);
        GameEngine engine = createEngine(new GridBoard(3, 3), firstPlayer, secondPlayer);
        GameView view = new RecordingGameView();

        assertThrows(NullPointerException.class, () -> new GameController(null, view, firstPlayer, secondPlayer));
        assertThrows(NullPointerException.class, () -> new GameController(engine, null, firstPlayer, secondPlayer));
        assertThrows(NullPointerException.class, () -> new GameController(engine, view, null, secondPlayer));
        assertThrows(NullPointerException.class, () -> new GameController(engine, view, firstPlayer, null));
    }

    private static GameEngine createEngine(Board board, Player firstPlayer, Player secondPlayer) {
        return new GameEngine(board, new ClassicTicTacToeRules(), new StandardMoveResolver(), firstPlayer, secondPlayer);
    }

    private static Player scriptedPlayer(String name, CellState state, int... cellNumbers) {
        Deque<Integer> moves = new ArrayDeque<>();

        for (int cellNumber : cellNumbers) {
            moves.addLast(cellNumber);
        }

        PositionMapper mapper = new RowMajorPositionMapper();
        MoveSource source = (ignoredPlayer, board) -> mapper.toPosition(moves.removeFirst(), board);

        return new HumanPlayer(name, state, source);
    }

    private static Player unusedPlayer(String name, CellState state) {
        MoveSource source = (ignoredPlayer, ignoredBoard) -> {
            throw new AssertionError("This player should not have been asked for a move.");
        };

        return new HumanPlayer(name, state, source);
    }

    private static List<String> namesOf(List<Player> players) {
        return players.stream().map(Player::getName).toList();
    }

    private static List<MoveStatus> statusesOf(List<TurnResult> turnResults) {
        return turnResults.stream().map(result -> result.moveResolution().status()).toList();
    }

    private static final class RecordingGameView implements GameView {
        private final List<String> events = new ArrayList<>();
        private final List<Board> displayedBoards = new ArrayList<>();
        private final List<Player> displayedTurns = new ArrayList<>();
        private final List<TurnResult> moveResults = new ArrayList<>();
        private GameOutcome outcome;
        private Player outcomeFirstPlayer;
        private Player outcomeSecondPlayer;

        @Override
        public void displayWelcome() {
            events.add("welcome");
        }

        @Override
        public void displayBoard(Board board) {
            displayedBoards.add(board.copy());
            events.add("board");
        }

        @Override
        public void displayTurn(Player player) {
            displayedTurns.add(player);
            events.add("turn:" + player.getName());
        }

        @Override
        public void displayMoveResult(TurnResult turnResult) {
            moveResults.add(turnResult);
            events.add("result:" + turnResult.moveResolution().status());
        }

        @Override
        public void displayOutcome(GameOutcome outcome, Player firstPlayer, Player secondPlayer) {
            this.outcome = outcome;
            outcomeFirstPlayer = firstPlayer;
            outcomeSecondPlayer = secondPlayer;
            events.add("outcome:" + outcome);
        }
    }
}
