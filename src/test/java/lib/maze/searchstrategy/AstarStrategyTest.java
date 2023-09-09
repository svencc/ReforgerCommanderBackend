package lib.maze.searchstrategy;

import lib.maze.Maze;
import lib.maze.MazeLocation;
import lib.maze.Node;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.fail;

class AstarStrategyTest {

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
        final AstarStrategy searchStrategy = new AstarStrategy();
        final Optional<Node<MazeLocation>> dsfSolution1 = searchStrategy.search(testMaze.getStart(), testMaze::goalTest, testMaze::successors, testMaze::manhattanDistance);

        if (dsfSolution1.isEmpty()) {
            fail("No solution found!");
        } else {
            final List<MazeLocation> path1 = Node.nodeToPath(dsfSolution1.get());
            testMaze.mark(path1);
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
        final AstarStrategy searchStrategy = new AstarStrategy();
        final Optional<Node<MazeLocation>> dsfSolution1 = searchStrategy.search(testMaze.getStart(), testMaze::goalTest, testMaze::successors, testMaze::manhattanDistance);

        if (dsfSolution1.isEmpty()) {
            fail("No solution found!");
        } else {
            final List<MazeLocation> path1 = Node.nodeToPath(dsfSolution1.get());
            testMaze.mark(path1);
            System.out.println(testMaze);
        }
    }

    @Test
    void randomMaze() {
        // Arrange
        final double sparseness = 0.35; // 0.2 means 20% of the maze will be blocked

        final Maze testMaze = Maze.randomMaze(11, 11, MazeLocation.builder().row(7).column(0).build(), MazeLocation.builder().row(3).column(10).build(), sparseness);

        // Act
        final AstarStrategy searchStrategy = new AstarStrategy();
        final Optional<Node<MazeLocation>> astarSolution = searchStrategy.search(testMaze.getStart(), testMaze::goalTest, testMaze::successors, testMaze::manhattanDistance);

        if (astarSolution.isEmpty()) {
            System.out.println("#################################");
            System.out.println("# Probably not solvable         #");
            System.out.println("#################################");
            System.out.println(testMaze);
        } else {
            final List<MazeLocation> path = Node.nodeToPath(astarSolution.get());
            testMaze.mark(path);
            System.out.println(testMaze);
        }
    }

}