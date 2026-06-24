package edu.csc214.portfolio.tictactoe;

import java.util.List;
import java.util.Objects;

/**
 * Coordinates player turns, move processing, and game-outcome evaluation.
 *
 * <p>The engine depends on abstractions for board storage, game rules, move
 * resolution, and event listeners. It contains no console, graphical, file,
 * or computer-player-specific behavior.</p>
 */
public final class GameEngine {

    // Big idea:  This is the glue between the component abstractions like boar,
    // rules, players, listeners, etc and the user interface.  
    // Invalid moves leave the same player active.
    // Successful moves advance the round to the other player.
    // A move that ends the game does NOT advance the round to the other player.
    // Attempts after the game ends return GAME_ALREADY_OVER
    // Players and formatters receive board copies rather than direct access to the live board.
    // Every attempted turn can be observed by future logging or history components.

    // I think I have every thing covered.  Maybe.

    private final Board board;
    private final GameRules rules;
    private final MoveResolver moveResolver;
    private final List<Player> players;
    private final List<GameListener> listeners;

    private int currentPlayerIndex;
    private GameOutcome gameOutcome;

    public GameEngine(Board board, GameRules rules, MoveResolver moveResolver, Player firstPlayer, Player secondPlayer) {
        this(board, rules, moveResolver, firstPlayer, secondPlayer, List.of());
    }

    public GameEngine(Board board, GameRules rules, MoveResolver moveResolver, Player firstPlayer, Player secondPlayer, List<GameListener> listeners) {
        this.board = Objects.requireNonNull(board, "Board cannot be null.");
        this.rules = Objects.requireNonNull(rules, "Game rules cannot be null.");
        this.moveResolver = Objects.requireNonNull(moveResolver, "Move resolver cannot be null.");

        Objects.requireNonNull(firstPlayer, "First player cannot be null.");
        Objects.requireNonNull(secondPlayer, "Second player cannot be null.");

        if (firstPlayer.getCellState() == secondPlayer.getCellState()) {
            throw new IllegalArgumentException("Players must use different cell states.");
        }

        this.players = List.of(firstPlayer, secondPlayer);
        this.listeners = copyListeners(listeners);
        this.currentPlayerIndex = 0;
        this.gameOutcome = rules.evaluate(board);
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public GameOutcome getGameOutcome() {
        return gameOutcome;
    }

    public boolean isGameOver() {
        return gameOutcome != GameOutcome.IN_PROGRESS;
    }

    public Board getBoardSnapshot() {
        return board.copy();
    }

    public TurnResult playMove(Position position) {
        Objects.requireNonNull(position, "Position cannot be null.");

        Player actingPlayer = getCurrentPlayer();
        MoveResolution resolution;

        if (isGameOver()) {
            resolution = new MoveResolution(MoveStatus.GAME_ALREADY_OVER, false);
        } else {
            resolution = moveResolver.resolve(board, position, actingPlayer.getCellState());

            if (resolution.turnConsumed()) {
                gameOutcome = rules.evaluate(board);

                if (!isGameOver()) {
                    advancePlayer();
                }
            }
        }

        TurnResult result = new TurnResult(actingPlayer, position, resolution, gameOutcome);
        notifyListeners(result);
        return result;
    }

    private void advancePlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    private void notifyListeners(TurnResult result) {
        for (GameListener listener : listeners) {
            listener.onTurnCompleted(result);
        }
    }

    private static List<GameListener> copyListeners(List<GameListener> listeners) {
        Objects.requireNonNull(listeners, "Game listeners cannot be null.");

        for (GameListener listener : listeners) {
            Objects.requireNonNull(listener, "Game listeners cannot contain null.");
        }

        return List.copyOf(listeners);
    }
}
