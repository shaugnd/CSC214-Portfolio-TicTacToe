package edu.csc214.portfolio.tictactoe;

/**
 * Receives notification after a player attempts a turn.
 *
 * <p>Listeners may record moves, update statistics, build replay histories, or
 * perform other secondary tasks without adding those responsibilities to the
 * game engine.</p>
 */
@FunctionalInterface
public interface GameListener {
    // Big idea:  Keep the game engine as simple as possible by encapsulating
    // as much of the operational complexity as possible while enabling later
    // add on features to respond to whatever chaotic monkey wrench Professor
    // tosses into the machinery.
    
    void onTurnCompleted(TurnResult turnResult);
}
