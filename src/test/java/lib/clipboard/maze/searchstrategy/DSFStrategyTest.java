package lib.clipboard.maze.searchstrategy;

import lib.clipboard.maze.Maze;
import lib.clipboard.maze.MazeLocation;
import lib.clipboard.maze.Node;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.fail;

class DSFStrategyTest {

    @Test
    void maze1() {
        // Arrange
        final String mazeMap = """
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
        final Maze testMaze = Maze.fromString(mazeMap);

        // Act
        final DSFStrategy searchStrategy = new DSFStrategy();
        final Optional<Node<MazeLocation>> dsfSolution = searchStrategy.search(testMaze.getStart(), testMaze::goalTest, testMaze::successors);

        if (dsfSolution.isEmpty()) {
            fail("No solution found!");
        } else {
            final List<MazeLocation> path = Node.nodeToPath(dsfSolution.get());
            testMaze.mark(path);
            System.out.println(testMaze);
        }
    }

    @Test
    void maze2() {
        // Arrange
        final String mazeMap = """
                [ ][ ][ ][ ][ ][X][ ][ ][ ][X][ ]
                [ ][ ][ ][X][ ][X][ ][ ][ ][ ][ ]
                [ ][ ][ ][X][ ][X][ ][X][ ][X][ ]
                [X][X][ ][X][ ][X][ ][X][ ][ ][ ]
                [ ][ ][ ][ ][ ][X][ ][X][ ][X][ ]
                [S][X][X][X][ ][X][ ][X][ ][X][G]
                [ ][ ][ ][X][ ][X][ ][X][ ][X][X]
                [X][X][ ][X][ ][X][ ][ ][ ][X][ ]
                [ ][ ][ ][X][ ][X][ ][X][ ][ ][ ]
                [ ][ ][ ][X][ ][ ][ ][ ][ ][ ][ ]
                [ ][ ][ ][ ][ ][X][ ][X][ ][ ][ ]
                """.stripIndent();
        final Maze testMaze = Maze.fromString(mazeMap);

        // Act
        final DSFStrategy searchStrategy = new DSFStrategy();
        final Optional<Node<MazeLocation>> dsfSolution = searchStrategy.search(testMaze.getStart(), testMaze::goalTest, testMaze::successors);

        if (dsfSolution.isEmpty()) {
            fail("No solution found!");
        } else {
            final List<MazeLocation> path = Node.nodeToPath(dsfSolution.get());
            testMaze.mark(path);
            System.out.println(testMaze);
        }
    }

}