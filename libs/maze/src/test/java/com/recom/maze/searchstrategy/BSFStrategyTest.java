package com.recom.maze.searchstrategy;

import com.recom.maze.Maze;
import com.recom.maze.MazeLocation;
import com.recom.maze.Node;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.fail;

class BSFStrategyTest {

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
        final BSFStrategy searchStrategy = new BSFStrategy();
        final Optional<Node<MazeLocation>> bsfSolution = searchStrategy.search(testMaze.getStart(), testMaze::goalTest, testMaze::successors);

        if (bsfSolution.isEmpty()) {
            fail("No solution found!");
        } else {
            final List<MazeLocation> path = Node.nodeToPath(bsfSolution.get());
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
        final BSFStrategy searchStrategy = new BSFStrategy();
        final Optional<Node<MazeLocation>> bsfSolution = searchStrategy.search(testMaze.getStart(), testMaze::goalTest, testMaze::successors);

        if (bsfSolution.isEmpty()) {
            fail("No solution found!");
        } else {
            final List<MazeLocation> path = Node.nodeToPath(bsfSolution.get());
            testMaze.mark(path);
            System.out.println(testMaze);
        }
    }

}