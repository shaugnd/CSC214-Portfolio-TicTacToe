package edu.csc214.portfolio.tictactoe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Selects Tic-Tac-Toe moves by applying a fixed sequence of opportunistic rules.
 *
 * <p>The player chooses an opening corner, takes the center after one move when
 * possible, wins immediately when it can, blocks an immediate loss, and
 * otherwise selects a random legal position.</p>
 */
public final class OpportunisticComputerPlayer extends Player {
    private final GameRules rules;
    private final Random random;

    public OpportunisticComputerPlayer(String name, CellState cellState, GameRules rules) {
        this(name, cellState, rules, new Random());
    }

    public OpportunisticComputerPlayer(String name, CellState cellState, GameRules rules, Random random) {
        super(name, cellState);
        this.rules = Objects.requireNonNull(rules, "Game rules cannot be null.");
        this.random = Objects.requireNonNull(random, "Random source cannot be null.");
    }

    @Override
    public Position chooseMove(Board board) {
        Objects.requireNonNull(board, "Board cannot be null.");

        List<Position> availablePositions = board.getAvailablePositions();

        if (availablePositions.isEmpty()) {
            throw new IllegalStateException("No available positions remain.");
        }

        int occupiedCellCount = board.getRowCount() * board.getColumnCount() - availablePositions.size();

        if (occupiedCellCount == 0) {
            return chooseRandomPosition(availableCorners(board));
        }

        if (occupiedCellCount == 1) {
            Position center = new Position(board.getRowCount() / 2, board.getColumnCount() / 2);

            if (board.isAvailable(center)) {
                return center;
            }
        }

        Position winningMove = findWinningMove(board, getCellState());

        if (winningMove != null) {
            return winningMove;
        }

        Position blockingMove = findWinningMove(board, opponentState());

        if (blockingMove != null) {
            return blockingMove;
        }

        return chooseRandomPosition(availablePositions);
    }

    private Position findWinningMove(Board board, CellState testedState) {
        for (Position position : board.getAvailablePositions()) {
            Board testBoard = board.copy();
            testBoard.place(position, testedState);

            if (rules.evaluate(testBoard) == winningOutcome(testedState)) {
                return position;
            }
        }

        return null;
    }

    private List<Position> availableCorners(Board board) {
        int lastRow = board.getRowCount() - 1;
        int lastColumn = board.getColumnCount() - 1;
        List<Position> corners = List.of(
                new Position(0, 0),
                new Position(0, lastColumn),
                new Position(lastRow, 0),
                new Position(lastRow, lastColumn));

        List<Position> availableCorners = new ArrayList<>();

        for (Position corner : corners) {
            if (board.isAvailable(corner) && !availableCorners.contains(corner)) {
                availableCorners.add(corner);
            }
        }

        if (availableCorners.isEmpty()) {
            throw new IllegalStateException("An empty board must contain an available corner.");
        }

        return availableCorners;
    }

    private Position chooseRandomPosition(List<Position> positions) {
        return positions.get(random.nextInt(positions.size()));
    }

    private CellState opponentState() {
        return getCellState() == CellState.PLAYER_ONE ? CellState.PLAYER_TWO : CellState.PLAYER_ONE;
    }

    private static GameOutcome winningOutcome(CellState state) {
        return state == CellState.PLAYER_ONE
                ? GameOutcome.PLAYER_ONE_WINS
                : GameOutcome.PLAYER_TWO_WINS;
    }
}
