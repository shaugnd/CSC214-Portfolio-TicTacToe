package edu.csc214.portfolio.tictactoe;

/**
 * Launches the console version of Tic-Tac-Toe.
 *
 * <p>The entry point assembles the production dependencies while game rules,
 * board state, player behavior, presentation, and session flow remain in their
 * dedicated classes.</p>
 */
public final class App {

    private App() {
    }

    public static void main(String[] args) {
        ConsoleMoveSource consoleInput = new ConsoleMoveSource();
        GameView gameView = new ConsoleGameView();

        GameControllerFactory gameControllerFactory = () -> {
            Player firstPlayer = new HumanPlayer("Player 1", CellState.PLAYER_ONE, consoleInput);
            Player secondPlayer = new HumanPlayer("Player 2", CellState.PLAYER_TWO, consoleInput);

            Board board = new GridBoard(3, 3);
            GameRules rules = new ClassicTicTacToeRules();
            MoveResolver moveResolver = new StandardMoveResolver();
            GameEngine gameEngine = new GameEngine(board, rules, moveResolver, firstPlayer, secondPlayer);

            return new GameController(gameEngine, gameView, firstPlayer, secondPlayer);
        };

        GameSessionController sessionController = new GameSessionController(gameControllerFactory, consoleInput, gameView);
        sessionController.playSession();
    }
}
