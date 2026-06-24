package edu.csc214.portfolio.tictactoe;

/**
 * Launches the console version of Tic-Tac-Toe.
 *
 * <p>The entry point assembles the production dependencies while game rules,
 * board state, player behavior, presentation, and application flow remain in
 * their dedicated classes.</p>
 */
public final class App {

    private App() {
    }

    public static void main(String[] args) {
        MoveSource moveSource = new ConsoleMoveSource();

        Player firstPlayer = new HumanPlayer("Player 1", CellState.PLAYER_ONE, moveSource);
        Player secondPlayer = new HumanPlayer("Player 2", CellState.PLAYER_TWO, moveSource);

        Board board = new GridBoard(3, 3);
        GameRules rules = new ClassicTicTacToeRules();
        MoveResolver moveResolver = new StandardMoveResolver();
        GameEngine gameEngine = new GameEngine(board, rules, moveResolver, firstPlayer, secondPlayer);
        GameView gameView = new ConsoleGameView();

        GameController controller = new GameController(gameEngine, gameView, firstPlayer, secondPlayer);
        controller.playGame();
    }
}
