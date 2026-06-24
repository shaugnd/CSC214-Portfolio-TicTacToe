package edu.csc214.portfolio.tictactoe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

/**
 * Tests player identity, constructor validation, and delegated move selection
 * in {@link HumanPlayer}.
 *
 * <p>The test cases follow the ZOMBIE method: Zero, One, Many, Boundary,
 * Interface, and Exception.</p>
 */
class HumanPlayerTest {
    // I feel like there are a couple more edge cases here, but I cannot quite put my finger on them.  
    // TODO:  Come back and revisit edge cases.
    
    // Zero

    @Test
    void moveSelectionDoesNotChangeBoard() {
        Board board = new GridBoard(3, 3);
        HumanPlayer player = new HumanPlayer("Alex", CellState.PLAYER_ONE, (ignoredPlayer, ignoredBoard) -> new Position(1, 1));

        player.chooseMove(board);

        assertEquals(9, board.getAvailablePositions().size());
    }

    // One

    @Test
    void storesPlayerName() {
        HumanPlayer player = new HumanPlayer("Alex", CellState.PLAYER_ONE, (ignoredPlayer, ignoredBoard) -> new Position(0, 0));

        assertEquals("Alex", player.getName());
    }

    @Test
    void storesPlayerCellState() {
        HumanPlayer player = new HumanPlayer("Blair", CellState.PLAYER_TWO, (ignoredPlayer, ignoredBoard) -> new Position(0, 0));

        assertEquals(CellState.PLAYER_TWO, player.getCellState());
    }

    @Test
    void returnsPositionProvidedByMoveSource() {
        Position expected = new Position(2, 1);
        HumanPlayer player = new HumanPlayer("Alex", CellState.PLAYER_ONE, (ignoredPlayer, ignoredBoard) -> expected);

        assertEquals(expected, player.chooseMove(new GridBoard(3, 3)));
    }

    // Many

    @Test
    void passesPlayerAndBoardToMoveSource() {
        Board board = new GridBoard(3, 3);
        AtomicReference<Player> receivedPlayer = new AtomicReference<>();
        AtomicReference<Board> receivedBoard = new AtomicReference<>();
        MoveSource moveSource = (player, suppliedBoard) -> {
            receivedPlayer.set(player);
            receivedBoard.set(suppliedBoard);
            return new Position(1, 2);
        };
        HumanPlayer player = new HumanPlayer("Alex", CellState.PLAYER_ONE, moveSource);

        player.chooseMove(board);

        assertSame(player, receivedPlayer.get());
        assertSame(board, receivedBoard.get());
    }

    // Boundary

    @Test
    void stripsLeadingAndTrailingWhitespaceFromName() {
        HumanPlayer player = new HumanPlayer("  Alex  ", CellState.PLAYER_ONE, (ignoredPlayer, ignoredBoard) -> new Position(0, 0));

        assertEquals("Alex", player.getName());
    }

    // Interface

    @Test
    void humanPlayerCanBeUsedThroughPlayerType() {
        Player player = new HumanPlayer("Alex", CellState.PLAYER_ONE, (ignoredPlayer, ignoredBoard) -> new Position(2, 2));

        assertEquals(new Position(2, 2), player.chooseMove(new GridBoard(3, 3)));
    }

    // Exception

    @Test
    void rejectsNullName() {
        assertThrows(IllegalArgumentException.class, () -> new HumanPlayer(null, CellState.PLAYER_ONE, (player, board) -> new Position(0, 0)));
    }

    @Test
    void rejectsBlankName() {
        assertThrows(IllegalArgumentException.class, () -> new HumanPlayer("   ", CellState.PLAYER_ONE, (player, board) -> new Position(0, 0)));
    }

    @Test
    void rejectsNullCellState() {
        assertThrows(NullPointerException.class, () -> new HumanPlayer("Alex", null, (player, board) -> new Position(0, 0)));
    }

    @Test
    void rejectsEmptyCellState() {
        assertThrows(IllegalArgumentException.class, () -> new HumanPlayer("Alex", CellState.EMPTY, (player, board) -> new Position(0, 0)));
    }

    @Test
    void rejectsNullMoveSource() {
        assertThrows(NullPointerException.class, () -> new HumanPlayer("Alex", CellState.PLAYER_ONE, null));
    }

    @Test
    void chooseMoveRejectsNullBoard() {
        HumanPlayer player = new HumanPlayer("Alex", CellState.PLAYER_ONE, (ignoredPlayer, ignoredBoard) -> new Position(0, 0));

        assertThrows(NullPointerException.class, () -> player.chooseMove(null));
    }

    @Test
    void rejectsNullPositionReturnedByMoveSource() {
        HumanPlayer player = new HumanPlayer("Alex", CellState.PLAYER_ONE, (ignoredPlayer, ignoredBoard) -> null);

        assertThrows(NullPointerException.class, () -> player.chooseMove(new GridBoard(3, 3)));
    }
}
