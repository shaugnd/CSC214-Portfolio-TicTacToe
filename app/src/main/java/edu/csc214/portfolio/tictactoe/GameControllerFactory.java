package edu.csc214.portfolio.tictactoe;

/**
 * Creates a controller for a new game of Tic-Tac-Toe.
 *
 * <p>Each call should return a controller backed by fresh game state so that a
 * replay begins with a cleared board and no state from the previous game.</p>
 */
@FunctionalInterface
public interface GameControllerFactory {
    // This will be useful if we have to be able to change game board size on the fly
    // or something crazy like that.
    GameController createGame();
}
