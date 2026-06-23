package edu.csc214.portfolio.tictactoe;

/**
 * Identifies a location on a two-dimensional game board.
 *
 * <p>A position stores coordinates only. The board is responsible for deciding whether those coordinates are within its boundaries.</p>
 *
 * @param row the zero-based row coordinate
 * @param column the zero-based column coordinate
 */
public record Position(int row, int column) {
    // Position models an immutable pair of coordinate values.
    // A record seems to be the most code efficient approach here.  
    // It gives me two private final fields, a constructor, accessor 
    // methods, an equals() method, matching functionality and a
    // toString() which may or may not be useful.  A class would
    // require a bunch more code to get the same functionality.  
    // 
    // The .equals() will be especially useful when it comes to 
    // JUnit testing.
}
