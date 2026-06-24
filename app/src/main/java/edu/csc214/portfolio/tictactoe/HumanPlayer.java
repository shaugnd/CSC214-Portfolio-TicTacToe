package edu.csc214.portfolio.tictactoe;

import java.util.Objects;

/**
 * Represents a human-controlled player whose moves are supplied by an external
 * input source.
 *
 * <p>The player remains independent of console, graphical, or test input. The
 * injected {@link MoveSource} determines how each move is obtained.</p>
 */
public final class HumanPlayer extends Player {
    private final MoveSource moveSource;

    public HumanPlayer(String name, CellState cellState, MoveSource moveSource) {
        super(name, cellState);
        this.moveSource = Objects.requireNonNull(moveSource, "Move source cannot be null.");
    }

    @Override
    public Position chooseMove(Board board) {
        Objects.requireNonNull(board, "Board cannot be null.");
        return Objects.requireNonNull(moveSource.requestMove(this, board), 
                            "Move source cannot return a null position.");
    }
}





