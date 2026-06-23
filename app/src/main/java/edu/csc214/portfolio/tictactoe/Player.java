package edu.csc214.portfolio.tictactoe;

import java.util.Objects;

/**
 * Represents one participant in a game.
 *
 * <p>Each player has a display name and a logical cell state. Subclasses decide how a move is selected, allowing human and computer-controlled players to be used through the same abstraction.</p>
 */
public abstract class Player {
    // Just defining a player here.  This gives me room to have computer or human players by
    // creating the class for each.  This architecture could even support an alien player?  
    // Not sure what that would look like.
    
    private final String name;
    private final CellState cellState;

    protected Player(String name, CellState cellState) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Player name cannot be blank.");
        }

        this.cellState = Objects.requireNonNull(cellState, "Cell state cannot be null.");

        if (cellState == CellState.EMPTY) {
            throw new IllegalArgumentException("A player cannot use the EMPTY cell state.");
        }

        this.name = name.strip();
    }

    public final String getName() {
        return name;
    }

    public final CellState getCellState() {
        return cellState;
    }

    public abstract Position chooseMove(Board board);
}
