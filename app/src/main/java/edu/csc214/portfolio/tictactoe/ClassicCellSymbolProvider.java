package edu.csc214.portfolio.tictactoe;

import java.util.Objects;

/**
 * Supplies the traditional text symbols used by classic Tic-Tac-Toe.
 *
 * <p>Player one is displayed as X, player two is displayed as O, and an empty
 * cell is displayed as a blank space.</p>
 */
public final class ClassicCellSymbolProvider implements CellSymbolProvider {

    @Override
    public String getSymbol(CellState state) {
        Objects.requireNonNull(state, "Cell state cannot be null.");

        return switch (state) {
            case EMPTY -> " ";
            case PLAYER_ONE -> "X";
            case PLAYER_TWO -> "O";
        };
    }
}


