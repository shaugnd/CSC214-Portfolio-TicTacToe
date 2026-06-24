package edu.csc214.portfolio.tictactoe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests turn order, move handling, game outcomes, board snapshots, listeners,
 * and defensive validation in {@link GameEngine}.
 *
 * <p>The test cases follow the ZOMBIE method: Zero, One, Many, Boundary,
 * Interface, and Exception.</p>
 */
class GameEngineTest {

    // Zero

    @Test
    void newGameStartsInProgress() {
        GameEngine engine = createEngine();

        assertEquals(GameOutcome.IN_PROGRESS, engine.getGameOutcome());
        assertFalse(engine.isGameOver());
    }

    @Test
    void newGameStartsWithFirstPlayer() {
        Player firstPlayer = player("Alex", CellState.PLAYER_ONE);
        Player secondPlayer = player("Blair", CellState.PLAYER_TWO);
        GameEngine engine = createEngine(firstPlayer, secondPlayer);

        assertSame(firstPlayer, engine.getCurrentPlayer());
    }

    @Test
    void newGameBoardContainsZeroMarks() {
        GameEngine engine = createEngine();

        assertEquals(9, engine.getBoardSnapshot().getAvailablePositions().size());
    }

    // One

    @Test
    void oneSuccessfulMoveIsPlaced() {
        GameEngine engine = createEngine();

        TurnResult result = engine.playMove(new Position(1, 1));

        assertEquals(MoveStatus.PLACED, result.moveResolution().status());
        assertEquals(CellState.PLAYER_ONE, engine.getBoardSnapshot().getCell(new Position(1, 1)));
    }

    @Test
    void oneSuccessfulMoveAdvancesToSecondPlayer() {
        Player firstPlayer = player("Alex", CellState.PLAYER_ONE);
        Player secondPlayer = player("Blair", CellState.PLAYER_TWO);
        GameEngine engine = createEngine(firstPlayer, secondPlayer);

        engine.playMove(new Position(0, 0));

        assertSame(secondPlayer, engine.getCurrentPlayer());
    }

    @Test
    void oneInvalidMoveLeavesFirstPlayerActive() {
        Player firstPlayer = player("Alex", CellState.PLAYER_ONE);
        Player secondPlayer = player("Blair", CellState.PLAYER_TWO);
        GameEngine engine = createEngine(firstPlayer, secondPlayer);

        TurnResult result = engine.playMove(new Position(-1, 0));

        assertEquals(MoveStatus.OUT_OF_BOUNDS, result.moveResolution().status());
        assertSame(firstPlayer, engine.getCurrentPlayer());
    }

    // Many

    @Test
    void successfulMovesAlternatePlayers() {
        Player firstPlayer = player("Alex", CellState.PLAYER_ONE);
        Player secondPlayer = player("Blair", CellState.PLAYER_TWO);
        GameEngine engine = createEngine(firstPlayer, secondPlayer);

        engine.playMove(new Position(0, 0));
        assertSame(secondPlayer, engine.getCurrentPlayer());

        engine.playMove(new Position(1, 1));
        assertSame(firstPlayer, engine.getCurrentPlayer());

        engine.playMove(new Position(2, 2));
        assertSame(secondPlayer, engine.getCurrentPlayer());
    }

    @Test
    void occupiedMoveDoesNotAdvancePlayer() {
        Player firstPlayer = player("Alex", CellState.PLAYER_ONE);
        Player secondPlayer = player("Blair", CellState.PLAYER_TWO);
        GameEngine engine = createEngine(firstPlayer, secondPlayer);

        engine.playMove(new Position(0, 0));
        TurnResult result = engine.playMove(new Position(0, 0));

        assertEquals(MoveStatus.OCCUPIED, result.moveResolution().status());
        assertFalse(result.moveResolution().turnConsumed());
        assertSame(secondPlayer, engine.getCurrentPlayer());
    }

    @Test
    void detectsPlayerOneWin() {
        GameEngine engine = createEngine();

        engine.playMove(new Position(0, 0));
        engine.playMove(new Position(1, 0));
        engine.playMove(new Position(0, 1));
        engine.playMove(new Position(1, 1));
        TurnResult result = engine.playMove(new Position(0, 2));

        assertEquals(GameOutcome.PLAYER_ONE_WINS, result.gameOutcome());
        assertEquals(GameOutcome.PLAYER_ONE_WINS, engine.getGameOutcome());
        assertTrue(engine.isGameOver());
    }

    @Test
    void detectsPlayerTwoWin() {
        GameEngine engine = createEngine();

        engine.playMove(new Position(0, 0));
        engine.playMove(new Position(1, 0));
        engine.playMove(new Position(0, 1));
        engine.playMove(new Position(1, 1));
        engine.playMove(new Position(2, 2));
        TurnResult result = engine.playMove(new Position(1, 2));

        assertEquals(GameOutcome.PLAYER_TWO_WINS, result.gameOutcome());
        assertTrue(engine.isGameOver());
    }

    @Test
    void detectsDrawAfterNinthMove() {
        GameEngine engine = createEngine();

        engine.playMove(new Position(0, 0));
        engine.playMove(new Position(0, 1));
        engine.playMove(new Position(0, 2));
        engine.playMove(new Position(1, 1));
        engine.playMove(new Position(1, 0));
        engine.playMove(new Position(1, 2));
        engine.playMove(new Position(2, 1));
        engine.playMove(new Position(2, 0));
        TurnResult result = engine.playMove(new Position(2, 2));

        assertEquals(GameOutcome.DRAW, result.gameOutcome());
        assertTrue(engine.isGameOver());
    }

    @Test
    void listenerReceivesEveryAttemptedTurn() {
        List<TurnResult> recordedTurns = new ArrayList<>();
        GameEngine engine = createEngine(List.of(recordedTurns::add));

        engine.playMove(new Position(0, 0));
        engine.playMove(new Position(0, 0));
        engine.playMove(new Position(1, 1));

        assertEquals(3, recordedTurns.size());
        assertEquals(MoveStatus.PLACED, recordedTurns.get(0).moveResolution().status());
        assertEquals(MoveStatus.OCCUPIED, recordedTurns.get(1).moveResolution().status());
        assertEquals(MoveStatus.PLACED, recordedTurns.get(2).moveResolution().status());
    }

    // Boundary

    @Test
    void winningPlayerRemainsCurrentAfterGameEnds() {
        Player firstPlayer = player("Alex", CellState.PLAYER_ONE);
        Player secondPlayer = player("Blair", CellState.PLAYER_TWO);
        GameEngine engine = createEngine(firstPlayer, secondPlayer);

        engine.playMove(new Position(0, 0));
        engine.playMove(new Position(1, 0));
        engine.playMove(new Position(0, 1));
        engine.playMove(new Position(1, 1));
        engine.playMove(new Position(0, 2));

        assertSame(firstPlayer, engine.getCurrentPlayer());
    }

    @Test
    void moveAttemptAfterGameEndsIsRejected() {
        GameEngine engine = createEngine();

        engine.playMove(new Position(0, 0));
        engine.playMove(new Position(1, 0));
        engine.playMove(new Position(0, 1));
        engine.playMove(new Position(1, 1));
        engine.playMove(new Position(0, 2));

        TurnResult result = engine.playMove(new Position(2, 2));

        assertEquals(MoveStatus.GAME_ALREADY_OVER, result.moveResolution().status());
        assertFalse(result.moveResolution().turnConsumed());
        assertEquals(CellState.EMPTY, engine.getBoardSnapshot().getCell(new Position(2, 2)));
    }

    @Test
    void engineRecognizesInitiallyCompletedBoard() {
        Board board = new GridBoard(3, 3);
        board.place(new Position(0, 0), CellState.PLAYER_ONE);
        board.place(new Position(0, 1), CellState.PLAYER_ONE);
        board.place(new Position(0, 2), CellState.PLAYER_ONE);

        GameEngine engine = new GameEngine(board, new ClassicTicTacToeRules(), new StandardMoveResolver(), player("Alex", CellState.PLAYER_ONE), player("Blair", CellState.PLAYER_TWO));

        assertEquals(GameOutcome.PLAYER_ONE_WINS, engine.getGameOutcome());
        assertTrue(engine.isGameOver());
    }

    // Interface

    @Test
    void boardSnapshotIsIndependentOfLiveBoard() {
        GameEngine engine = createEngine();
        engine.playMove(new Position(0, 0));

        Board snapshot = engine.getBoardSnapshot();
        snapshot.place(new Position(1, 1), CellState.PLAYER_TWO);

        assertEquals(CellState.EMPTY, engine.getBoardSnapshot().getCell(new Position(1, 1)));
        assertEquals(CellState.PLAYER_TWO, snapshot.getCell(new Position(1, 1)));
    }

    @Test
    void eachSnapshotIsASeparateObject() {
        GameEngine engine = createEngine();

        Board firstSnapshot = engine.getBoardSnapshot();
        Board secondSnapshot = engine.getBoardSnapshot();

        assertNotSame(firstSnapshot, secondSnapshot);
    }

    @Test
    void engineUsesDependenciesThroughTheirInterfaces() {
        Board board = new GridBoard(3, 3);
        GameRules rules = new ClassicTicTacToeRules();
        MoveResolver resolver = new StandardMoveResolver();
        GameEngine engine = new GameEngine(board, rules, resolver, player("Alex", CellState.PLAYER_ONE), player("Blair", CellState.PLAYER_TWO));

        assertEquals(MoveStatus.PLACED, engine.playMove(new Position(1, 1)).moveResolution().status());
    }

    @Test
    void constructorCopiesListenerList() {
        List<GameListener> listeners = new ArrayList<>();
        List<TurnResult> recordedTurns = new ArrayList<>();
        listeners.add(recordedTurns::add);

        GameEngine engine = createEngine(listeners);
        listeners.clear();
        engine.playMove(new Position(0, 0));

        assertEquals(1, recordedTurns.size());
    }

    // Exception

    @Test
    void playMoveRejectsNullPosition() {
        GameEngine engine = createEngine();

        assertThrows(NullPointerException.class, () -> engine.playMove(null));
    }

    @Test
    void constructorRejectsNullDependencies() {
        Player firstPlayer = player("Alex", CellState.PLAYER_ONE);
        Player secondPlayer = player("Blair", CellState.PLAYER_TWO);

        assertThrows(NullPointerException.class, () -> new GameEngine(null, new ClassicTicTacToeRules(), new StandardMoveResolver(), firstPlayer, secondPlayer));
        assertThrows(NullPointerException.class, () -> new GameEngine(new GridBoard(3, 3), null, new StandardMoveResolver(), firstPlayer, secondPlayer));
        assertThrows(NullPointerException.class, () -> new GameEngine(new GridBoard(3, 3), new ClassicTicTacToeRules(), null, firstPlayer, secondPlayer));
        assertThrows(NullPointerException.class, () -> new GameEngine(new GridBoard(3, 3), new ClassicTicTacToeRules(), new StandardMoveResolver(), null, secondPlayer));
        assertThrows(NullPointerException.class, () -> new GameEngine(new GridBoard(3, 3), new ClassicTicTacToeRules(), new StandardMoveResolver(), firstPlayer, null));
    }

    @Test
    void constructorRejectsPlayersUsingSameCellState() {
        Player firstPlayer = player("Alex", CellState.PLAYER_ONE);
        Player secondPlayer = player("Blair", CellState.PLAYER_ONE);

        assertThrows(IllegalArgumentException.class, () -> createEngine(firstPlayer, secondPlayer));
    }

    @Test
    void constructorRejectsNullListenerList() {
        assertThrows(NullPointerException.class, () -> new GameEngine(new GridBoard(3, 3), new ClassicTicTacToeRules(), new StandardMoveResolver(), player("Alex", CellState.PLAYER_ONE), player("Blair", CellState.PLAYER_TWO), null));
    }

    @Test
    void constructorRejectsNullListenerElement() {
        List<GameListener> listeners = new ArrayList<>();
        listeners.add(null);

        assertThrows(NullPointerException.class, () -> createEngine(listeners));
    }

    private static GameEngine createEngine() {
        return createEngine(player("Alex", CellState.PLAYER_ONE), player("Blair", CellState.PLAYER_TWO));
    }

    private static GameEngine createEngine(Player firstPlayer, Player secondPlayer) {
        return new GameEngine(new GridBoard(3, 3), new ClassicTicTacToeRules(), new StandardMoveResolver(), firstPlayer, secondPlayer);
    }

    private static GameEngine createEngine(List<GameListener> listeners) {
        return new GameEngine(new GridBoard(3, 3), new ClassicTicTacToeRules(), new StandardMoveResolver(), player("Alex", CellState.PLAYER_ONE), player("Blair", CellState.PLAYER_TWO), listeners);
    }

    private static Player player(String name, CellState state) {
        return new HumanPlayer(name, state, (ignoredPlayer, ignoredBoard) -> new Position(0, 0));
    }
}
