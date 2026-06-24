package edu.csc214.portfolio.tictactoe;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Presents Tic-Tac-Toe through a text-based interface.
 *
 * <p>The view delegats board rendering and player-symbol selection to injected
 * collaborators. It does not read input, change game state, or evaluate game
 * rules.</p>
 */
public final class ConsoleGameView implements GameView {
    private final PrintWriter writer;
    private final BoardFormatter boardFormatter;
    private final CellSymbolProvider symbolProvider;

    public ConsoleGameView() {
        this(new OutputStreamWriter(System.out, StandardCharsets.UTF_8), new ConsoleBoardFormatter(new RowMajorPositionMapper(), new ClassicCellSymbolProvider()), new ClassicCellSymbolProvider());
    }

    ConsoleGameView(Writer writer, BoardFormatter boardFormatter, CellSymbolProvider symbolProvider) {
        this.writer = new PrintWriter(Objects.requireNonNull(writer, "Writer cannot be null."), true);
        this.boardFormatter = Objects.requireNonNull(boardFormatter, "Board formatter cannot be null.");
        this.symbolProvider = Objects.requireNonNull(symbolProvider, "Cell symbol provider cannot be null.");
    }

    @Override
    public void displayWelcome() {
        writer.println("Welcome to Tic-Tac-Toe!");
        writer.println("Choose a numbered cell to place your mark.");
    }

    @Override
    public void displayBoard(Board board) {
        Objects.requireNonNull(board, "Board cannot be null.");

        writer.println();
        writer.println(boardFormatter.format(board));
        writer.println();
    }

    @Override
    public void displayTurn(Player player) {
        Objects.requireNonNull(player, "Player cannot be null.");

        String symbol = symbolProvider.getSymbol(player.getCellState());
        writer.println(player.getName() + "'s turn (" + symbol + ").");
    }

    @Override
    public void displayMoveResult(TurnResult turnResult) {
        // The default branch here is intentional.  I'm hedging against the possibility
        // that professor might have some sort of turn forfeiting functionality so
        // we might need a future status and still be able toproduce a readable fallback
        // rather than forcing a class change right out of the gate.
        
        Objects.requireNonNull(turnResult, "Turn result cannot be null.");

        MoveStatus status = turnResult.moveResolution().status();

        if (status == MoveStatus.PLACED) {
            return;
        }

        String message = switch (status) {
            case OCCUPIED -> "That cell is already occupied. Try again.";
            case OUT_OF_BOUNDS -> "That cell is outside the board. Try again.";
            case GAME_ALREADY_OVER -> "The game is already over.";
            default -> "Move result: " + formatStatus(status);
        };

        writer.println(message);
    }

    @Override
    public void displayOutcome(GameOutcome outcome, Player firstPlayer, Player secondPlayer) {
        Objects.requireNonNull(outcome, "Game outcome cannot be null.");
        Objects.requireNonNull(firstPlayer, "First player cannot be null.");
        Objects.requireNonNull(secondPlayer, "Second player cannot be null.");

        String message = switch (outcome) {
            case IN_PROGRESS -> "The game is still in progress.";
            case DRAW -> "The game ends in a draw.";
            case PLAYER_ONE_WINS -> firstPlayer.getName() + " wins!";
            case PLAYER_TWO_WINS -> secondPlayer.getName() + " wins!";
        };

        writer.println(message);
    }

    private static String formatStatus(MoveStatus status) {
        return status.name().toLowerCase().replace('_', ' ');
    }
}
