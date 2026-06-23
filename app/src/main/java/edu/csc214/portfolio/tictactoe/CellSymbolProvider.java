package edu.csc214.portfolio.tictactoe;

/**
 * Supplies the display symbol associated with a logical cell state.
 *
 * <p>This abstraction keeps presentation choices such as X, O, numbers,
 * Unicode symbols, or other text representations outside the board model.</p>
 */
@FunctionalInterface
public interface CellSymbolProvider {
    // Big Idea:  Just in case Prof wants us to use emojis instead of x and o, or something like that.
    String getSymbol(CellState state);
}
