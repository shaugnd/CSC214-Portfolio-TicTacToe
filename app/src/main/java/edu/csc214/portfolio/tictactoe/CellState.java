package edu.csc214.portfolio.tictactoe;

/**
 * Represents the logical occupancy of one game-board cell.
 *
 * <p>The values identify which player owns a cell without defining how that
 * ownership is displayed by a console or graphical interface.</p>
 */
public enum CellState {
    // By using this enum and focusing on player ownership of a cell, I can insulate
    // myslef against the instructor throwing a curveball in P2 or P3 that requires using
    // alternate symbology in the game board.  I'll let the console handle the display layer
    // and keep the ownership logic separate.  This makes it easier to drop in a GUI based
    // console without changing the underlying class architeture.
    
    EMPTY,
    PLAYER_ONE,
    PLAYER_TWO
}
