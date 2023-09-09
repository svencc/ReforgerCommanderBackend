package lib.maze;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class Maze {

    private final int rows, columns;
    private final MazeLocation start, goal;
    private final Cell[][] grid;

    @Builder
    private Maze(
            final int rows,
            final int columns,
            @NonNull final MazeLocation start,
            @NonNull final MazeLocation goal,
            final double sparseness
    ) {
        this.rows = rows;
        this.columns = columns;
        this.start = start;
        this.goal = goal;
        this.grid = new Cell[rows][columns];
        for (final Cell[] row : grid) {
            Arrays.fill(row, Cell.EMPTY);
        }

        // fill the grid with blocked cells
        randomlyFill(sparseness);

        // fill the start and goal locations
        grid[start.getRow()][start.getColumn()] = Cell.START;
        grid[goal.getRow()][goal.getColumn()] = Cell.GOAL;
    }

    public static Maze randomMaze(
            final int rows,
            final int columns,
            @NonNull final MazeLocation start,
            @NonNull final MazeLocation goal,
            final double sparseness
    ) {
        return new Maze(rows, columns, start, goal, sparseness);
    }

    private void randomlyFill(double sparseness) {
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                if (Math.random() < sparseness) {
                    grid[row][column] = Cell.BLOCKED;
                }
            }
        }
    }

    @Builder
    public Maze(
            final int rows,
            final int columns,
            @NonNull final MazeLocation start,
            @NonNull final MazeLocation goal,
            final Cell[][] grid
    ) {
        this.rows = rows;
        this.columns = columns;
        this.start = start;
        this.goal = goal;
        this.grid = grid;
    }

    @NonNull
    public static Maze fromString(@NonNull final String mazeString) throws IllegalArgumentException {
        final String[] rows = mazeString.split("\n");
        int numRows = rows.length;
        int numCols = rows[0].length() / 3; // every cell is represented by 3 characters: "[ ]"

        MazeLocation start = null;
        MazeLocation goal = null;
        final Cell[][] grid = new Cell[numRows][numCols];

        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                final String symbol = rows[r].substring(c * 3, c * 3 + 3); // The 3-character sequence representing the cell type is: "[X]" or "[ ]" or "[S]" or "[G]"
                final Cell cell = Cell.fromSymbol(symbol);
                grid[r][c] = cell;
                switch (cell) {
                    case START -> start = MazeLocation.builder().row(r).column(c).build();
                    case GOAL -> goal = MazeLocation.builder().row(r).column(c).build();
                }
            }
        }

        if (start == null || goal == null) {
            throw new IllegalArgumentException("The maze must have a start and a goal location");
        }

        return new Maze(numRows, numCols, start, goal, grid);
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();
        for (final Cell[] row : grid) {
            for (final Cell cell : row) {
                stringBuilder.append(cell.toString());
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    public boolean goalTest(@NonNull final MazeLocation location) {
        return goal.equals(location);
    }

    @NonNull
    public List<MazeLocation> successors(@NonNull final MazeLocation location) {
        final List<MazeLocation> successors = new ArrayList<>();

        // look DOWN:
        // First check if we are not on the bottom edge of the grid
        // than check if the cell below is not blocked
        if (location.getRow() + 1 < rows && grid[location.getRow() + 1][location.getColumn()] != Cell.BLOCKED) {
            successors.add(MazeLocation.builder()
                    .row(location.getRow() + 1)
                    .column(location.getColumn())
                    .build());
        }

        // look UP:
        // First check if we are not on the top edge of the grid
        // than check if the cell above is not blocked
        if (location.getRow() - 1 >= 0 && grid[location.getRow() - 1][location.getColumn()] != Cell.BLOCKED) {
            successors.add(MazeLocation.builder()
                    .row(location.getRow() - 1)
                    .column(location.getColumn())
                    .build());
        }

        // look RIGHT:
        // First check if we are not on the right edge of the grid
        // than check if the cell to the right is not blocked
        if (location.getColumn() + 1 < columns && grid[location.getRow()][location.getColumn() + 1] != Cell.BLOCKED) {
            successors.add(MazeLocation.builder()
                    .row(location.getRow())
                    .column(location.getColumn() + 1)
                    .build());
        }

        // look LEFT:
        // First check if we are not on the left edge of the grid
        // than check if the cell to the left is not blocked
        if (location.getColumn() - 1 >= 0 && grid[location.getRow()][location.getColumn() - 1] != Cell.BLOCKED) {
            successors.add(MazeLocation.builder()
                    .row(location.getRow())
                    .column(location.getColumn() - 1)
                    .build());
        }

        return successors;
    }

    public void mark(@NonNull final List<MazeLocation> path) {
        for (final MazeLocation mazeLocation : path) {
            grid[mazeLocation.getRow()][mazeLocation.getColumn()] = Cell.PATH;
        }
        grid[start.getRow()][start.getColumn()] = Cell.START;
        grid[goal.getRow()][goal.getColumn()] = Cell.GOAL;
    }

    public void clear(@NonNull final List<MazeLocation> path) {
        for (final MazeLocation mazeLocation : path) {
            grid[mazeLocation.getRow()][mazeLocation.getColumn()] = Cell.EMPTY;
        }
        grid[start.getRow()][start.getColumn()] = Cell.START;
        grid[goal.getRow()][goal.getColumn()] = Cell.GOAL;
    }

    public double euclideanDistance(@NonNull final MazeLocation location) {
        int xDistance = location.getColumn() - goal.getColumn();
        int yDistance = location.getRow() - goal.getRow();

        return Math.sqrt(Math.pow(xDistance, 2) + Math.pow(yDistance, 2));
    }

    public double manhattanDistance(@NonNull final MazeLocation location) {
        int xDistance = Math.abs(location.getColumn() - goal.getColumn());
        int yDistance = Math.abs(location.getRow() - goal.getRow());

        return xDistance + yDistance;
    }

}
