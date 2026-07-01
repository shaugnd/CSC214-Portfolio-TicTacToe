package edu.csc214.portfolio.tictactoe;

/**
 * Supplies the player arrangement for a new Tic-Tac-Toe game.
 */
public interface GameTypePrompt {
    // This interface is not strictly necessary to make this work, but I think it is the proper
    // way to do it.
    GameType requestGameType();
}
