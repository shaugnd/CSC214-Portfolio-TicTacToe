package edu.csc214.portfolio.tictactoe;

/**
 * Defines the information presented by a game user interface.
 *
 * <p>The game controller depends on this abstraction rather than on console or
 * graphical output directly. Different interfaces may therefore present the
 * same game without changing the engine or domain model.</p>
 */
public interface GameView {
    void displayWelcome();

    void displayBoard(Board board);

    void displayTurn(Player player);

    void displayMoveResult(TurnResult turnResult);

    void displayOutcome(GameOutcome outcome, Player firstPlayer, Player secondPlayer);
}
