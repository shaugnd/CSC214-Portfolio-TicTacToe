package edu.csc214.portfolio.tictactoe;

import java.util.Objects;

/**
 * Describes the immediate result of processing a move.
 *
 * <p>The status explains what happened, while {@code turnConsumed} tells the
 * game engine whether play should advance to the next player. </p>
 *
 * @param status the result of the move attempt
 * @param turnConsumed whether the current player's turn was consumed
 */
public record MoveResolution(MoveStatus status, boolean turnConsumed) {
// This may not be strictly necessary, but I want to insulate against the instructor dropping
// a move consuming or forfeit type game condition.  Move resolution allows me to pair 
// move status with whether the move consumed the player's turn or not.

    public MoveResolution {
        Objects.requireNonNull(status, "Move status cannot be null.");
    }
}
