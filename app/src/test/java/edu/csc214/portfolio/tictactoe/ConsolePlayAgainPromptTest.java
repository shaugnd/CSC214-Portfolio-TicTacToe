package edu.csc214.portfolio.tictactoe;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.StringReader;
import java.io.StringWriter;

import org.junit.jupiter.api.Test;

/**
 * Tests validated replay decisions supplied by {@link ConsoleMoveSource}.
 *
 * <p>The tests verify accepted yes/no responses, case insensitivity,
 * whitespace handling, invalid-entry retries, and end-of-input behavior.</p>
 */
class ConsolePlayAgainPromptTest {

    @Test
    void lowercaseYesReturnsTrue() {
        PlayAgainPrompt prompt = promptWith("yes\n", new StringWriter());

        assertTrue(prompt.requestPlayAgain());
    }

    @Test
    void mixedCaseYesResponsesReturnTrue() {
        String[] responses = {"YES", "YeS", "yeS", "yEs"};

        for (String response : responses) {
            PlayAgainPrompt prompt = promptWith(response + "\n", new StringWriter());
            assertTrue(prompt.requestPlayAgain());
        }
    }

    @Test
    void lowercaseNoReturnsFalse() {
        PlayAgainPrompt prompt = promptWith("no\n", new StringWriter());

        assertFalse(prompt.requestPlayAgain());
    }

    @Test
    void mixedCaseNoResponsesReturnFalse() {
        String[] responses = {"NO", "No", "nO"};

        for (String response : responses) {
            PlayAgainPrompt prompt = promptWith(response + "\n", new StringWriter());
            assertFalse(prompt.requestPlayAgain());
        }
    }

    @Test
    void surroundingWhitespaceIsIgnored() {
        PlayAgainPrompt yesPrompt = promptWith("   YES   \n", new StringWriter());
        PlayAgainPrompt noPrompt = promptWith("\tNo\t\n", new StringWriter());

        assertTrue(yesPrompt.requestPlayAgain());
        assertFalse(noPrompt.requestPlayAgain());
    }

    @Test
    void invalidResponsesAreRejectedUntilYesIsEntered() {
        StringWriter output = new StringWriter();
        PlayAgainPrompt prompt = promptWith("\nfoobar\n123\n$\n3.5\nYES\n", output);

        assertTrue(prompt.requestPlayAgain());
        assertEquals(5, countOccurrences(output.toString(), "That is not a valid entry!"));
        assertEquals(6, countOccurrences(output.toString(), "Would you like to play again (yes/no)?"));
    }

    @Test
    void abbreviatedResponsesAreRejected() {
        StringWriter output = new StringWriter();
        PlayAgainPrompt prompt = promptWith("y\nn\nno\n", output);

        assertFalse(prompt.requestPlayAgain());
        assertEquals(2, countOccurrences(output.toString(), "That is not a valid entry!"));
    }

    @Test
    void worksThroughPlayAgainPromptInterface() {
        PlayAgainPrompt prompt = promptWith("yes\n", new StringWriter());

        assertTrue(prompt.requestPlayAgain());
    }

    @Test
    void endOfInputBeforeDecisionThrowsException() {
        PlayAgainPrompt prompt = promptWith("", new StringWriter());

        assertThrows(IllegalStateException.class, prompt::requestPlayAgain);
    }

    private static PlayAgainPrompt promptWith(String input, StringWriter output) {
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
