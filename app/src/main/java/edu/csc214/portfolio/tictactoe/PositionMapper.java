package edu.csc214.portfolio.tictactoe;

/**
 * Converts between user-facing cell numbers and board positions.
 *
 * <p>A shared mapper keeps board rendering and move input consistent. Different
 * mapping strategies may be introduced without changing the board, players, or
 * game engine.</p>
 */
public interface PositionMapper {
    // The Big Idea here is to build an interface to separate how the game state
    // is displayed or otherwise communicated to the user, from how that information
    // is stored internally.  For example, it is conceivable that you might have a
    // version of the interface which is friendly to the visually impaired and uses 
    // a primarily audio interface.  By decoupling the presentation layer from the 
    // rest of the processing, you get more flexibility.  Also, testing can be much more 
    // comprehensive and is easier.
    
    int toCellNumber(Position position, Board board);

    Position toPosition(int cellNumber, Board board);
}
