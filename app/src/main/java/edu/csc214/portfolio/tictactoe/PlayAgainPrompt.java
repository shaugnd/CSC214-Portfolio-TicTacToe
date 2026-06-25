package edu.csc214.portfolio.tictactoe;

/**
 * Requests the user's decision about starting another game.
 *
 * <p>The session controller depends on this abstraction so replay decisions may
 * come from a console, graphical interface, or controlled test implementation.</p>
 */
@FunctionalInterface   // I want to be able to use this with a lambda.
public interface PlayAgainPrompt {
    boolean requestPlayAgain();
}
