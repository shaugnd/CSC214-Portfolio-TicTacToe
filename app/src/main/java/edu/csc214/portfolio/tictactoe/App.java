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
            GameType gameType = consoleInput.requestGameType();
            Board board = new GridBoard(3, 3);
            GameRules rules = new ClassicTicTacToeRules();
            MoveResolver moveResolver = new StandardMoveResolver();

            Player firstPlayer;
            Player secondPlayer;

            switch (gameType) {
                case HUMAN_VS_HUMAN -> {
                    firstPlayer = new HumanPlayer("Player 1", CellState.PLAYER_ONE, consoleInput);
                    secondPlayer = new HumanPlayer("Player 2", CellState.PLAYER_TWO, consoleInput);
                }
                case HUMAN_VS_COMPUTER -> {
                    firstPlayer = new HumanPlayer("Player 1", CellState.PLAYER_ONE, consoleInput);
                    secondPlayer = new OpportunisticComputerPlayer("Computer", CellState.PLAYER_TWO, rules);
                }
                case COMPUTER_VS_HUMAN -> {
                    firstPlayer = new OpportunisticComputerPlayer("Computer", CellState.PLAYER_ONE, rules);
                    secondPlayer = new HumanPlayer("Player 2", CellState.PLAYER_TWO, consoleInput);
                }
                default -> throw new IllegalStateException("Unsupported game type: " + gameType);
            }

            GameEngine gameEngine = new GameEngine(
                    board,
                    rules,
                    moveResolver,
                    firstPlayer,
                    secondPlayer);

            return new GameController(gameEngine, gameView, firstPlayer, secondPlayer);
        };

        GameSessionController sessionController =
                new GameSessionController(gameControllerFactory, consoleInput, gameView);

        sessionController.playSession();
    }
}
