package edu.csc214.portfolio.tictactoe;

/**
 * Supplies a move for a player.
 *
 * <p>Move sources isolate input mechanisms from player and game logic.
 * Production code may use console input, while tests may provide predetermined
 * moves without messing around with System.in.</p>
 */
public interface MoveSource {
    Position requestMove(Player player, Board board);
}
