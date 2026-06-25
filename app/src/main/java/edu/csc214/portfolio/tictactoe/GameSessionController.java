package edu.csc214.portfolio.tictactoe;

import java.util.Objects;

/**
 * Coordinates a session containing one or more games of Tic-Tac-Toe.
 *
 * <p>Each round is created through the configured factory so replaying starts
 * with a new board, engine, players, and controller rather than resetting a
 * completed game.</p>
 */
public final class GameSessionController {
    // This is quite a lot of encapsulation for such a simple game, but we
    // are not just coding a game here.  We are building a resilient architecture
    // that can handle new requirments with a minimum of fuss and little or no
    // editing of existing classes.  The nature of the assignment is using SOLID
    // for Defensive Design.  It is way overkill for this game, but this is just
    // an exercise.
    private final GameControllerFactory gameControllerFactory;
    private final PlayAgainPrompt playAgainPrompt;
    private final GameView gameView;

    public GameSessionController(GameControllerFactory gameControllerFactory, PlayAgainPrompt playAgainPrompt, GameView gameView) {
        this.gameControllerFactory = Objects.requireNonNull(gameControllerFactory, "Game controller factory cannot be null.");
        this.playAgainPrompt = Objects.requireNonNull(playAgainPrompt, "Play-again prompt cannot be null.");
        this.gameView = Objects.requireNonNull(gameView, "Game view cannot be null.");
    }

    public void playSession() {
        gameView.displayWelcome();

        boolean playAgain;
        // I very rarely find a good usecase for a do-while, so I'll use it when it makes sense.
        do {
            GameController gameController = Objects.requireNonNull(gameControllerFactory.createGame(), "Game controller factory cannot return null.");
            gameController.playGame();
            playAgain = playAgainPrompt.requestPlayAgain();
        } while (playAgain);

        gameView.displayGoodbye();
    }
}
