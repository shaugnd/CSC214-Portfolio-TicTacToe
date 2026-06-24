package edu.csc214.portfolio.tictactoe;

import java.util.Objects;

/**
 * Processes ordinary board moves by placing a player's state in an available
 * position.
 *
 * <p>Out-of-bounds and occupied positions are reported without changing the
 * board or consuming the player's turn.</p>
 */
public final class StandardMoveResolver implements MoveResolver {
    // Here we implement MoveResolver is a way that complies with standard
    // game behavior.  If we get a requirement for obscure game behavior,
    // we can create a 'non-standar' move resolver to deal with it without haveing
    // to come back and update this code.
    
    @Override
    public MoveResolution resolve(Board board, Position position, CellState state) {
        Objects.requireNonNull(board, "Board cannot be null.");
        Objects.requireNonNull(position, "Position cannot be null.");
        Objects.requireNonNull(state, "Cell state cannot be null.");

        if (state == CellState.EMPTY) {
            throw new IllegalArgumentException("Cannot place the EMPTY cell state.");
        }

        if (!board.isWithinBounds(position)) {
            return new MoveResolution(MoveStatus.OUT_OF_BOUNDS, false);
        }

        if (!board.isAvailable(position)) {
            return new MoveResolution(MoveStatus.OCCUPIED, false);
        }

        board.place(position, state);
        return new MoveResolution(MoveStatus.PLACED, true);
    }
}
