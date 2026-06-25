package edu.csc214.portfolio.tictactoe;

import java.util.Objects;

/**
 * Coordinates one complete game between players, the game engine, and a view.
 *
 * <p>The controller contains application flow only. Move validation, board
 * mutation, and outcome evaluation remain inside the game engine and its
 * collaborators.</p>
 */
public final class GameController {
    // Here the board is displayed whenever a turn is actually  consumed rather
    // than when a mark is placed on the board.  This will come in handy if there
    // is a future requirement which involved turn forfeiture without game
    // board mutation.
    private final GameEngine gameEngine;
    private final GameView gameView;
    private final Player firstPlayer;
    private final Player secondPlayer;

    public GameController(GameEngine gameEngine, GameView gameView, Player firstPlayer, Player secondPlayer) {
        this.gameEngine = Objects.requireNonNull(gameEngine, "Game engine cannot be null.");
        this.gameView = Objects.requireNonNull(gameView, "Game view cannot be null.");
        this.firstPlayer = Objects.requireNonNull(firstPlayer, "First player cannot be null.");
        this.secondPlayer = Objects.requireNonNull(secondPlayer, "Second player cannot be null.");
    }

    public void playGame() {
        gameView.displayBoard(gameEngine.getBoardSnapshot());

        while (!gameEngine.isGameOver()) {
            Player currentPlayer = gameEngine.getCurrentPlayer();
            gameView.displayTurn(currentPlayer);

            Position position = currentPlayer.chooseMove(gameEngine.getBoardSnapshot());
            TurnResult turnResult = gameEngine.playMove(position);

            gameView.displayMoveResult(turnResult);
            gameView.displayBoard(gameEngine.getBoardSnapshot());
        }

        gameView.displayOutcome(gameEngine.getGameOutcome(), firstPlayer, secondPlayer);
    }
}
