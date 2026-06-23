package edu.csc214.portfolio.tictactoe;

/**
 * Defines how an attempted move affects the board and whether it consumes the
 * current player's turn.
 *
 * <p>The game engine delegates move processing to this abstraction so that
 * alternate move effects may be introduced without changing the engine.</p>
 */
public interface MoveResolver {
    // Here we separate the effect of a selected move from the game engine.
    // This way, a diabolical professor can't break our code by inserting
    // some sort of move trap or move penalty requirement.
    
    MoveResolution resolve(Board board, Position position, CellState state);
}
