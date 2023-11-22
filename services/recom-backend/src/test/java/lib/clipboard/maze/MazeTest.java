package lib.clipboard.maze;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MazeTest {

    @Test
    void testToString_withEmptyMaze() {
        // Arrange
        final String expectedMaze = """
                [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
                [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
                [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
                [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
                [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
                [S][ ][ ][ ][ ][ ][ ][ ][ ][ ][G]
                [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
                [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
                [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
                [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
                [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
                """.stripIndent();

        // Act
        final Maze mazeToTest = Maze.builder()
                .rows(11)
                .columns(11)
                .start(MazeLocation.builder()
                        .row(5)
                        .column(0)
                        .build())
                .goal(MazeLocation.builder()
                        .row(5)
                        .column(10)
                        .build())
                .sparseness(0)
                .build();

        final String stringifiedMaze = mazeToTest.toString();

        // Assert
        assertEquals(expectedMaze, stringifiedMaze);
    }

    @Test
    void testToString_withFullMaze() {
        // Arrange
        final String expectedMaze = """
                [X][X][X][X][X][X][X][X][X][X][X]
                [X][X][X][X][X][X][X][X][X][X][X]
                [X][X][X][X][X][X][X][X][X][X][X]
                [X][X][X][X][X][X][X][X][X][X][X]
                [X][X][X][X][X][X][X][X][X][X][X]
                [S][X][X][X][X][X][X][X][X][X][G]
                [X][X][X][X][X][X][X][X][X][X][X]
                [X][X][X][X][X][X][X][X][X][X][X]
                [X][X][X][X][X][X][X][X][X][X][X]
                [X][X][X][X][X][X][X][X][X][X][X]
                [X][X][X][X][X][X][X][X][X][X][X]
                """.stripIndent();

        // Act
        final Maze mazeToTest = Maze.builder()
                .rows(11)
                .columns(11)
                .start(MazeLocation.builder()
                        .row(5)
                        .column(0)
                        .build())
                .goal(MazeLocation.builder()
                        .row(5)
                        .column(10)
                        .build())
                .sparseness(1)
                .build();

        final String stringifiedMaze = mazeToTest.toString();

        // Assert
        assertEquals(expectedMaze, stringifiedMaze);
    }

    @Test
    void testToString_withRandomMaze() {
        // Arrange
        final String expectedMaze = """
                [X][X][X][X][X][X][X][X][X][X]
                [X][X][X][X][X][X][X][X][X][X]
                [X][X][X][X][X][X][X][X][X][X]
                [X][X][X][X][X][X][X][X][X][X]
                [X][X][X][X][X][X][X][X][X][X]
                [S][X][X][X][X][X][X][X][X][G]
                [X][X][X][X][X][X][X][X][X][X]
                [X][X][X][X][X][X][X][X][X][X]
                [X][X][X][X][X][X][X][X][X][X]
                [X][X][X][X][X][X][X][X][X][X]
                """.stripIndent();

        // Act
        final Maze mazeToTest = Maze.builder()
                .rows(11)
                .columns(11)
                .start(MazeLocation.builder()
                        .row(5)
                        .column(0)
                        .build())
                .goal(MazeLocation.builder()
                        .row(5)
                        .column(10)
                        .build())
                .sparseness(0.5)
                .build();

        final String stringifiedMaze = mazeToTest.toString();

        // Assert
        int nrOfBlockedCells = countCharOccurrences(stringifiedMaze, 'X');
        assertTrue(30 <= nrOfBlockedCells && nrOfBlockedCells <= 80);
    }

    private static int countCharOccurrences(final String text, final char charToCount) {
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == charToCount) {
                count++;
            }
        }
        return count;
    }

    @Test
    void fromGivenString() {
        // Arrange
        final String mazePlan = """
                [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
                [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
                [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
                [ ][ ][ ][ ][X][ ][ ][ ][ ][ ][ ]
                [ ][ ][ ][ ][ ][X][ ][ ][ ][G][ ]
                [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
                [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
                [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
                [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
                [ ][S][ ][ ][ ][ ][ ][ ][ ][ ][ ]
                [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
                """.stripIndent();

        // Act & Assert
        assertEquals(mazePlan, Maze.fromString(mazePlan).toString());
    }

    @Test
    void successors_whenOnHorizontalEdge() {
        // Arrange
        final String usedMaze = """
                [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
                [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
                [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
                [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
                [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
                [S][ ][ ][ ][ ][ ][ ][ ][ ][ ][G]
                [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
                [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
                [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
                [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
                [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
                """.stripIndent();
        final Maze mazeToTest = Maze.fromString(usedMaze);

        List<MazeLocation> successors;
        // Act && Assert LEFT
        successors = mazeToTest.successors(mazeToTest.getStart());
        assertEquals(3, successors.size());
        assertTrue(successors.stream().anyMatch((x) -> x.getRow() == 4 && x.getColumn() == 0));
        assertTrue(successors.stream().anyMatch((x) -> x.getRow() == 5 && x.getColumn() == 1));
        assertTrue(successors.stream().anyMatch((x) -> x.getRow() == 6 && x.getColumn() == 0));

        // Act && Assert RIGHT
        successors = mazeToTest.successors(mazeToTest.getGoal());
        assertEquals(3, successors.size());
        assertTrue(successors.stream().anyMatch((x) -> x.getRow() == 4 && x.getColumn() == 10));
        assertTrue(successors.stream().anyMatch((x) -> x.getRow() == 5 && x.getColumn() == 9));
        assertTrue(successors.stream().anyMatch((x) -> x.getRow() == 6 && x.getColumn() == 10));
    }

    @Test
    void successors_whenOnHorizontalEdgeWithWalls_havingNoSuccessors() {
        // Arrange
        final String usedMaze = """
                [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
                [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
                [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
                [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
                [X][ ][ ][ ][ ][ ][ ][ ][ ][ ][X]
                [S][X][ ][ ][ ][ ][ ][ ][ ][X][G]
                [X][ ][ ][ ][ ][ ][ ][ ][ ][ ][X]
                [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
                [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
                [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
                [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
                """.stripIndent();
        final Maze mazeToTest = Maze.fromString(usedMaze);

        // Act && Assert LEFT
        assertEquals(0, mazeToTest.successors(mazeToTest.getStart()).size());

        // Act && Assert RIGHT
        assertEquals(0, mazeToTest.successors(mazeToTest.getStart()).size());
    }

    @Test
    void successors_whenOnVerticalEdge() {
        // Arrange
        final String usedMaze = """
                [ ][ ][ ][ ][ ][S][ ][ ][ ][ ][ ]
                [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
                [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
                [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
                [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
                [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
                [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
                [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
                [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
                [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
                [ ][ ][ ][ ][ ][G][ ][ ][ ][ ][ ]
                """.stripIndent();
        final Maze mazeToTest = Maze.fromString(usedMaze);

        List<MazeLocation> successors;
        // Act && Assert TOP
        successors = mazeToTest.successors(mazeToTest.getStart());
        assertEquals(3, successors.size());
        assertTrue(successors.stream().anyMatch((x) -> x.getRow() == 0 && x.getColumn() == 4));
        assertTrue(successors.stream().anyMatch((x) -> x.getRow() == 1 && x.getColumn() == 5));
        assertTrue(successors.stream().anyMatch((x) -> x.getRow() == 0 && x.getColumn() == 6));

        // Act && Assert BOTTOM
        successors = mazeToTest.successors(mazeToTest.getGoal());
        assertEquals(3, successors.size());
        assertTrue(successors.stream().anyMatch((x) -> x.getRow() == 10 && x.getColumn() == 4));
        assertTrue(successors.stream().anyMatch((x) -> x.getRow() == 9 && x.getColumn() == 5));
        assertTrue(successors.stream().anyMatch((x) -> x.getRow() == 10 && x.getColumn() == 6));
    }

    @Test
    void successors_whenOnVerticalEdgeWithWalls_havingNoSuccessors() {
        // Arrange
        final String usedMaze = """
                [ ][ ][ ][ ][X][S][X][ ][ ][ ][ ]
                [ ][ ][ ][ ][ ][X][ ][ ][ ][ ][ ]
                [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
                [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
                [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
                [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
                [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
                [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
                [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
                [ ][ ][ ][ ][ ][X][ ][ ][ ][ ][ ]
                [ ][ ][ ][ ][X][G][X][ ][ ][ ][ ]
                """.stripIndent();
        final Maze mazeToTest = Maze.fromString(usedMaze);

        // Act && Assert TOP
        assertEquals(0, mazeToTest.successors(mazeToTest.getStart()).size());

        // Act && Assert BOTTOM
        assertEquals(0, mazeToTest.successors(mazeToTest.getGoal()).size());
    }

    @Test
    void goalTest() {
        // Arrange
        final String usedMaze = """
                [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
                [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
                [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
                [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
                [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
                [S][ ][ ][ ][ ][ ][ ][ ][ ][ ][G]
                [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
                [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
                [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
                [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
                [ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
                """.stripIndent();
        final Maze mazeToTest = Maze.fromString(usedMaze);

        final MazeLocation newInstanceToCompare = MazeLocation.builder()
                .column(mazeToTest.getGoal().getColumn())
                .row(mazeToTest.getGoal().getRow())
                .build();


        // Act && Assert TOP
        assertTrue(mazeToTest.goalTest(mazeToTest.getGoal()));
        assertTrue(mazeToTest.goalTest(newInstanceToCompare));
    }

}