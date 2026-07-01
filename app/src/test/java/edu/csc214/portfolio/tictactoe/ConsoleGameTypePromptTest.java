package edu.csc214.portfolio.tictactoe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.StringReader;
import java.io.StringWriter;

import org.junit.jupiter.api.Test;

/**
 * Verifies console selection of the player arrangement for each new game.
 *
 * <p>The tests cover valid choices, retry behavior, whitespace handling,
 * interface use, and end-of-input failure.</p>
 */
class ConsoleGameTypePromptTest {
// Shifting back to the old way of organizing test cases to remain consistent with the project.
    // One
    @Test
    void choiceOneSelectsHumanVsHuman() {
        GameTypePrompt prompt = promptWith("1\n", new StringWriter());

        assertEquals(GameType.HUMAN_VS_HUMAN, prompt.requestGameType());
    }

    @Test
    void choiceTwoSelectsHumanVsComputer() {
        GameTypePrompt prompt = promptWith("2\n", new StringWriter());

        assertEquals(GameType.HUMAN_VS_COMPUTER, prompt.requestGameType());
    }

    @Test
    void choiceThreeSelectsComputerVsHuman() {
        GameTypePrompt prompt = promptWith("3\n", new StringWriter());

        assertEquals(GameType.COMPUTER_VS_HUMAN, prompt.requestGameType());
    }

    // Many
    @Test
    void invalidEntriesRetryUntilAValidChoiceIsEntered() {
        StringWriter output = new StringWriter();
        GameTypePrompt prompt = promptWith("banana\n0\n4\n2\n", output);

        assertEquals(GameType.HUMAN_VS_COMPUTER, prompt.requestGameType());
        assertEquals(3, countOccurrences(output.toString(), "Please enter 1, 2, or 3."));
    }

    // Boundary
    @Test
    void whitespaceAroundChoiceIsIgnored() {
        GameTypePrompt prompt = promptWith(" 3 \n", new StringWriter());

        assertEquals(GameType.COMPUTER_VS_HUMAN, prompt.requestGameType());
    }

    // Interface
    @Test
    void promptDisplaysAllThreeGameTypes() {
        StringWriter output = new StringWriter();
        GameTypePrompt prompt = promptWith("1\n", output);

        prompt.requestGameType();

        String text = output.toString();
        assertTrue(text.contains("1. Human vs. Human"));
        assertTrue(text.contains("2. Human vs. Computer"));
        assertTrue(text.contains("3. Computer vs. Human"));
    }

    // Exception
    @Test
    void endOfInputBeforeSelectionThrowsException() {
        GameTypePrompt prompt = promptWith("", new StringWriter());

        assertThrows(IllegalStateException.class, prompt::requestGameType);
    }

    private static GameTypePrompt promptWith(String input, StringWriter output) {
        return new ConsoleMoveSource(new StringReader(input), output, new RowMajorPositionMapper());
    }

    private static int countOccurrences(String text, String target) {
        int count = 0;
        int position = 0;

        while ((position = text.indexOf(target, position)) >= 0) {
            count++;
            position += target.length();
        }

        return count;
    }
}
