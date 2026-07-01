package edu.csc214.portfolio.tictactoe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Objects;

/**
 * Supplies human-player moves and replay decisions through text-based input.
 *
 * <p>The production constructor uses standard input and output. The injected
 * constructor allows tests or alternate text interfaces to provide controlled
 * readers and writers without replacing global system streams.</p>
 */
public final class ConsoleMoveSource implements MoveSource, PlayAgainPrompt, GameTypePrompt {
    // This component validates only the format and range of the entered cell 
    // number. Whether the selected cell is already occupied remains the 
    // responsibility of MoveResolver and GameEngine.

    // P2:  Added GameTypePrompt interface implementation.
    private final BufferedReader reader;
    private final PrintWriter writer;
    private final PositionMapper positionMapper;

    public ConsoleMoveSource() {
        this(new InputStreamReader(System.in, StandardCharsets.UTF_8), new OutputStreamWriter(System.out, StandardCharsets.UTF_8), new RowMajorPositionMapper());
    }

    ConsoleMoveSource(Reader reader, Writer writer, PositionMapper positionMapper) {
        this.reader = new BufferedReader(Objects.requireNonNull(reader, "Reader cannot be null."));
        this.writer = new PrintWriter(Objects.requireNonNull(writer, "Writer cannot be null."), true);
        this.positionMapper = Objects.requireNonNull(positionMapper, "Position mapper cannot be null.");
    }

    @Override
    public Position requestMove(Player player, Board board) {
        Objects.requireNonNull(player, "Player cannot be null.");
        Objects.requireNonNull(board, "Board cannot be null.");

        int cellCount = board.getRowCount() * board.getColumnCount();

        while (true) {
            writer.print(player.getName() + ", choose a cell (1-" + cellCount + "): ");
            writer.flush();

            String input = readLine();

            if (input == null) {
                throw new IllegalStateException("Input ended before a move was entered.");
            }

            try {
                int cellNumber = Integer.parseInt(input.strip());
                return positionMapper.toPosition(cellNumber, board);
            } catch (NumberFormatException | IndexOutOfBoundsException exception) {
                writer.println("Please enter a whole number between 1 and " + cellCount + ".");
            }
        }
    }

    // P2:  Added Game Type Request to comply with GameTypePrompt contract.
    
    @Override
    public GameType requestGameType() {
        while (true) {
            writer.println();
            writer.println("Choose a game type:");
            writer.println("1. Human vs. Human");
            writer.println("2. Human vs. Computer");
            writer.println("3. Computer vs. Human");
            writer.print("Enter 1, 2, or 3: ");
            writer.flush();

            String input = readLine();

            if (input == null) {
                throw new IllegalStateException("Input ended before a game type was selected.");
            }

            switch (input.strip()) {
                case "1":
                    return GameType.HUMAN_VS_HUMAN;
                case "2":
                    return GameType.HUMAN_VS_COMPUTER;
                case "3":
                    return GameType.COMPUTER_VS_HUMAN;
                default:
                    writer.println("Please enter 1, 2, or 3.");
            }
        }
    }

    @Override
    public boolean requestPlayAgain() {
        while (true) {
            writer.print("Would you like to play again (yes/no)? ");
            writer.flush();

            String input = readLine();

            if (input == null) {
                throw new IllegalStateException("Input ended before a replay decision was entered.");
            }

            String response = input.strip().toLowerCase(Locale.ROOT);

            if (response.equals("yes")) {
                return true;
            }

            if (response.equals("no")) {
                return false;
            }

            writer.println("That is not a valid entry!");
        }
    }

    private String readLine() {
        try {
            return reader.readLine();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read console input.", exception);
        }
    }
}
