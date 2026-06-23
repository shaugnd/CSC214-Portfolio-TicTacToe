package edu.csc214.portfolio.tictactoe;

import java.util.List;

/**
 * Defines the state-management operations of a game board.
 *
 * <p>A board stores logical cell ownership and validates positions and placement. It does not determine game outcomes, select moves, or control how the board is displayed.</p>
 */
public interface Board {
    // This is just the game board definition.  This protects against future requirements for different size
    // gameboards.  For the classic 3x3 game we will have a ClassicBoard that implements this interface, but 
    // a hypothetical 6x6 or 100x100 board class could also be created if required.
    // The methods are all of the things that I think a game board should respond to.  Console behavior and 
    // game specific winning rules are pushed upward in the object model.

    int getRowCount();

    int getColumnCount();

    boolean isWithinBounds(Position position);

    CellState getCell(Position position);

    boolean isAvailable(Position position);

    void place(Position position, CellState state);

    boolean isFull();

    // getAvailablePositions will support human play validation as well as computer
    // player algorithms.
    List<Position> getAvailablePositions();

    // I'm putting a copy() method here so that there is a functional way for 
    // a computer based opponent to do recursive game state analysis by making
    // a copy of the board and then changing the board state on the copy and 
    // evaluating whether that is a useful move or not. 

    Board copy();
}
