package edu.csc214.portfolio.tictactoe;

/**
 * Defines how a board state is evaluated to determine the current game
 * outcome.
 *
 * <p>A rule set examines a board without changing it. Different rule
 * implementations may support different board sizes, winning patterns, or
 * game variants.</p>
 */
public interface GameRules {
    // Basically, this interface separates game-state evaluation from board storage on the 
    // off chance that our instructor is particularly diabolical and wants to change the rules
    // of the game for P2 or P3.  With this architecture, different rulle sets can later evaluate
    // the same board implementation without modifying GridBoard.

    // Theoretically.  We will see how this all plays out.
    
    GameOutcome evaluate(Board board);
}
