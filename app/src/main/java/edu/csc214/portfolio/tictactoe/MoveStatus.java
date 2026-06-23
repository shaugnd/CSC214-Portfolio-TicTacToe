package edu.csc214.portfolio.tictactoe;

/**
 * Represents the result of attempting to process a player move.
 *
 * <p>Additional statuses may be introduced by future game variants without
 * changing the meaning of the existing values.</p>
 */
public enum MoveStatus {
    //Each round has a finite number of possible states.  Boolean or magic number
    // return values lead to illegible code, so I created this enum to make the
    // code a little more self explanatory and also restrict the possible values
    // and associated testing scenarios.
    
    PLACED,
    OCCUPIED,
    OUT_OF_BOUNDS,
    GAME_ALREADY_OVER
}
