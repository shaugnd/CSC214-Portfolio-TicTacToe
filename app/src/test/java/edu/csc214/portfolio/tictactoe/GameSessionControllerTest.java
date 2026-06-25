package edu.csc214.portfolio.tictactoe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

/**
 * Tests session-level replay behavior coordinated by
 * {@link GameSessionController}.
 *
 * <p>The tests verify that at least one game is played, replay decisions are
 * honored, each replay starts with fresh state, and session messages appear at
 * the correct times.</p>
 */
class GameSessionControllerTest {
    // This is probably overkill on the testing, but it's good practice to 
    //cover all of the bases as much as possible.  I'm certain that I am 
    //missing a test case or two here even with all this.
    @Test
    void noAfterFirstGameEndsSession() {
        RecordingGameView view = new RecordingGameView();
        AtomicInteger gamesCreated = new AtomicInteger();
        AtomicInteger promptsRequested = new AtomicInteger();

        GameControllerFactory factory = () -> {
            gamesCreated.incrementAndGet();
            return createWinningGame(view);
        };

        PlayAgainPrompt prompt = () -> {
            promptsRequested.incrementAndGet();
            return false;
        };

        new GameSessionController(factory, prompt, view).playSession();

        assertEquals(1, gamesCreated.get());
        assertEquals(1, promptsRequested.get());
        assertEquals(1, view.outcomes.size());
        assertEquals(1, countOccurrences(view.events, "welcome"));
        assertEquals(1, countOccurrences(view.events, "goodbye"));
        assertEquals("welcome", view.events.getFirst());
        assertEquals("goodbye", view.events.getLast());
    }

    @Test
    void yesThenNoRunsTwoCompleteGames() {
        RecordingGameView view = new RecordingGameView();
        AtomicInteger gamesCreated = new AtomicInteger();
        Deque<Boolean> responses = new ArrayDeque<>(List.of(true, false));

        GameControllerFactory factory = () -> {
            gamesCreated.incrementAndGet();
            return createWinningGame(view);
        };

        PlayAgainPrompt prompt = responses::removeFirst;

        new GameSessionController(factory, prompt, view).playSession();

        assertEquals(2, gamesCreated.get());
        assertEquals(2, view.outcomes.size());
        assertEquals(List.of(GameOutcome.PLAYER_ONE_WINS, GameOutcome.PLAYER_ONE_WINS), view.outcomes);
        assertEquals(1, countOccurrences(view.events, "welcome"));
        assertEquals(1, countOccurrences(view.events, "goodbye"));
    }

    @Test
    void eachReplayBeginsWithACompletelyEmptyBoard() {
        RecordingGameView view = new RecordingGameView();
        Deque<Boolean> responses = new ArrayDeque<>(List.of(true, false));
        List<Board> createdBoards = new ArrayList<>();

        GameControllerFactory factory = () -> {
            Board board = new GridBoard(3, 3);
            createdBoards.add(board);

            Player firstPlayer = scriptedPlayer("Player 1", CellState.PLAYER_ONE, 1, 2, 3);
            Player secondPlayer = scriptedPlayer("Player 2", CellState.PLAYER_TWO, 4, 5);
            GameEngine engine = new GameEngine(board, new ClassicTicTacToeRules(), new StandardMoveResolver(), firstPlayer, secondPlayer);

            return new GameController(engine, view, firstPlayer, secondPlayer);
        };

        new GameSessionController(factory, responses::removeFirst, view).playSession();

        assertEquals(2, createdBoards.size());
        assertNotSame(createdBoards.get(0), createdBoards.get(1));

        assertEquals(2, view.initialBoards.size());

        for (Board initialBoard : view.initialBoards) {
            assertEquals(9, initialBoard.getAvailablePositions().size());
            assertTrue(initialBoard.getAvailablePositions().containsAll(List.of(
                    new Position(0, 0),
                    new Position(0, 1),
                    new Position(0, 2),
                    new Position(1, 0),
                    new Position(1, 1),
                    new Position(1, 2),
                    new Position(2, 0),
                    new Position(2, 1),
                    new Position(2, 2))));
        }
    }

    @Test
    void welcomeIsDisplayedOnlyOnceAcrossSeveralGames() {
        RecordingGameView view = new RecordingGameView();
        Deque<Boolean> responses = new ArrayDeque<>(List.of(true, true, false));

        new GameSessionController(() -> createWinningGame(view), responses::removeFirst, view).playSession();

        assertEquals(3, view.outcomes.size());
        assertEquals(1, countOccurrences(view.events, "welcome"));
        assertEquals(1, countOccurrences(view.events, "goodbye"));
    }

    @Test
    void replayDecisionIsRequestedAfterEveryCompletedGame() {
        RecordingGameView view = new RecordingGameView();
        AtomicInteger promptCount = new AtomicInteger();
        Deque<Boolean> responses = new ArrayDeque<>(List.of(true, false));

        PlayAgainPrompt prompt = () -> {
            promptCount.incrementAndGet();
            assertEquals(promptCount.get(), view.outcomes.size());
            return responses.removeFirst();
        };

        new GameSessionController(() -> createWinningGame(view), prompt, view).playSession();

        assertEquals(2, promptCount.get());
    }

    @Test
    void factoryReturningNullIsRejected() {
        RecordingGameView view = new RecordingGameView();
        GameSessionController controller = new GameSessionController(() -> null, () -> false, view);

        assertThrows(NullPointerException.class, controller::playSession);
    }

    @Test
    void constructorRejectsNullDependencies() {
        RecordingGameView view = new RecordingGameView();
        GameControllerFactory factory = () -> createWinningGame(view);
        PlayAgainPrompt prompt = () -> false;

        assertThrows(NullPointerException.class, () -> new GameSessionController(null, prompt, view));
        assertThrows(NullPointerException.class, () -> new GameSessionController(factory, null, view));
        assertThrows(NullPointerException.class, () -> new GameSessionController(factory, prompt, null));
    }

    private static GameController createWinningGame(GameView view) {
        Player firstPlayer = scriptedPlayer("Player 1", CellState.PLAYER_ONE, 1, 2, 3);
        Player secondPlayer = scriptedPlayer("Player 2", CellState.PLAYER_TWO, 4, 5);
        GameEngine engine = new GameEngine(new GridBoard(3, 3), new ClassicTicTacToeRules(), new StandardMoveResolver(), firstPlayer, secondPlayer);

        return new GameController(engine, view, firstPlayer, secondPlayer);
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

    private static int countOccurrences(List<String> values, String target) {
        return (int) values.stream().filter(target::equals).count();
    }

    private static final class RecordingGameView implements GameView {
        private final List<String> events = new ArrayList<>();
        private final List<GameOutcome> outcomes = new ArrayList<>();
        private final List<Board> initialBoards = new ArrayList<>();

        @Override
        public void displayWelcome() {
            events.add("welcome");
        }

        @Override
        public void displayBoard(Board board) {
            if (events.getLast().equals("welcome") || events.getLast().startsWith("outcome:")) {
                initialBoards.add(board.copy());
            }

            events.add("board");
        }

        @Override
        public void displayTurn(Player player) {
            events.add("turn");
        }

        @Override
        public void displayMoveResult(TurnResult turnResult) {
            events.add("result");
        }

        @Override
        public void displayOutcome(GameOutcome outcome, Player firstPlayer, Player secondPlayer) {
            outcomes.add(outcome);
            events.add("outcome:" + outcome);
        }

        @Override
        public void displayGoodbye() {
            events.add("goodbye");
        }
    }
}
