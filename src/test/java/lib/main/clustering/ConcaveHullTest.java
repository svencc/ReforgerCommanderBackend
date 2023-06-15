package lib.main.clustering;

import lib.javaconcavehull.main.clustering.ConcaveHull;
import lib.javaconcavehull.main.clustering.Point;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ConcaveHullTest {
    final static Point[] stackOverflowCase = {
        new Point(1.01, 101.01),
        new Point(1.01, 101.5),
        new Point(1.5, 101.5),
        new Point(1.5, 101.01)
    };

    static final List<Point> stackOutList = new ArrayList<>(Arrays.asList(stackOverflowCase));

    @Test
    public void testStackOverflowCase() {
        final ConcaveHull c = new ConcaveHull();
        List<Point> orderedHull = c.calculateConcaveHull(stackOutList, 2);
        assertNotNull(orderedHull);
        System.out.println("Hull test from GitHub");
        for (Point p: orderedHull) {
            System.out.print(" " + p);
        }
    }
}