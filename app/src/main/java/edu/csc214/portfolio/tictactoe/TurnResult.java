package edu.csc214.portfolio.tictactoe;

import java.util.Objects;

/**
 * Describes the result of one attempted player turn.
 *
 * <p>The result identifies the acting player, selected position, move
 * resolution, and game outcome after the attempt. It contains no console or
 * file-output behavior.</p>
 *
 * @param player the player who attempted the move
 * @param position the selected board position
 * @param moveResolution the immediate result of processing the move
 * @param gameOutcome the game outcome after the move attempt
 */
public record TurnResult(Player player, Position position, MoveResolution moveResolution, GameOutcome gameOutcome) {
    // The big idea here is accounting.  With TurnResult, I can save an audit trail for
    // how a game got to a given state.  This enables, potential future requirements like
    // replay or statistics, or even undo functionality.  Not sure if any of that will be
    // strictly needed, but Professor could throw literally anything at us, so I'd rather
    // have the architecture in place to handly every curveball that I can think of so that 
    // I don't have to get all bandaids and bailing wire in P3.  I'll have an easier time
    // down the road if I over engineer the heck out of this thing here.
    
    public TurnResult {
        Objects.requireNonNull(player, "Player cannot be null.");
        Objects.requireNonNull(position, "Position cannot be null.");
        Objects.requireNonNull(moveResolution, "Move resolution cannot be null.");
        Objects.requireNonNull(gameOutcome, "Game outcome cannot be null.");
    }
}
