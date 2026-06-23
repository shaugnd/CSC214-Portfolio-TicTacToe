package edu.csc214.portfolio.tictactoe;

/**
 * Converts a board state into a presentation-ready string.
 *
 * <p>Formatting remains separate from board storage so that console,
 * graphical, or other views may represent the same logical board differently.</p>
 */
@FunctionalInterface
public interface BoardFormatter {
    String format(Board board);
}
