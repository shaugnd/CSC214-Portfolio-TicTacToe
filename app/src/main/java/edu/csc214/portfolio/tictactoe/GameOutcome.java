package edu.csc214.portfolio.tictactoe;

/**
 * Represents the current outcome of a Tic-Tac-Toe game.
 *
 * <p>The outcome remains independent of presentation so that console,
 * graphical, or other interfaces may display it differently.</p>
 */
public enum GameOutcome {
    // Different driver programs might want different behavior for game outcomes so
    // we use this enum to designate the game outcome and the driver can do whatever
    // it wants to do with that information.
    
    IN_PROGRESS,
    DRAW,
    PLAYER_ONE_WINS,
    PLAYER_TWO_WINS
}
