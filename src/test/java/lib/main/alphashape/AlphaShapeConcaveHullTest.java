package lib.main.alphashape;

import lib.javaconcavehull.main.alphashape.AlphaShapeConcaveHull;
import lib.javaconcavehull.main.concavehull.Point;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AlphaShapeConcaveHullTest {

    @Test
    public void testComputeConcaveHull() {

        // Create a list of points
//        final List<Point> cluster = new ArrayList<>(Arrays.asList(
//                Point.builder().x(0d).y(0d).build(),
//                Point.builder().x(1d).y(0d).build(),
//                Point.builder().x(1d).y(1d).build(),
//                Point.builder().x(0d).y(1d).build(),
//                Point.builder().x(0.5d).y(0.5d).build()
//        ));

        List<Point> cluster = new ArrayList<>(Arrays.asList(
                Point.builder().x(-5d + Math.random() * 0.2).y(Math.random() * 0.2).build(),
                Point.builder().x(-4d + Math.random() * 0.2).y(-1d + Math.random() * 0.2).build(),
                Point.builder().x(-4d + Math.random() * 0.2).y(-2d + Math.random() * 0.2).build(),
                Point.builder().x(-4d + Math.random() * 0.2).y(-3d + Math.random() * 0.2).build(),
                Point.builder().x(-4d + Math.random() * 0.2).y(-4d + Math.random() * 0.2).build(),
                Point.builder().x(-3d + Math.random() * 0.2).y(-4d + Math.random() * 0.2).build(),
                Point.builder().x(-2d + Math.random() * 0.2).y(-4d + Math.random() * 0.2).build(),
                Point.builder().x(-1d + Math.random() * 0.2).y(-4d + Math.random() * 0.2).build(),
                Point.builder().x(0d + Math.random() * 0.2).y(-4d + Math.random() * 0.2).build(),
                Point.builder().x(1d + Math.random() * 0.2).y(-4d + Math.random() * 0.2).build(),
                Point.builder().x(2d + Math.random() * 0.2).y(-4d + Math.random() * 0.2).build(),
                Point.builder().x(3d + Math.random() * 0.2).y(-4d + Math.random() * 0.2).build(),
                Point.builder().x(4d + Math.random() * 0.2).y(-4d + Math.random() * 0.2).build(),
                Point.builder().x(5d + Math.random() * 0.2).y(-4d + Math.random() * 0.2).build(),
                Point.builder().x(5d + Math.random() * 0.2).y(-3d + Math.random() * 0.2).build(),
                Point.builder().x(5d + Math.random() * 0.2).y(-2d + Math.random() * 0.2).build(),
                Point.builder().x(5d + Math.random() * 0.2).y(-1d + Math.random() * 0.2).build(),
                Point.builder().x(5d + Math.random() * 0.2).y(Math.random() * 0.2).build()
        ));


        // Compute the concave hull with alpha = 0.5
        final List<Point> concaveHull = AlphaShapeConcaveHull.computeConcaveHull(cluster, 0.000000001);

        // Expected concave hull points
        final List<Point> expectedHull = new ArrayList<>(Arrays.asList(
                Point.builder().x(0d).y(0d).build(),
                Point.builder().x(1d).y(0d).build(),
                Point.builder().x(1d).y(1d).build(),
                Point.builder().x(0d).y(1d).build()
        ));

        // Check if the computed concave hull matches the expected hull
        Assertions.assertEquals(expectedHull.size(), concaveHull.size());
        for (int i = 0; i < expectedHull.size(); i++) {
            Point expectedPoint = expectedHull.get(i);
            Point actualPoint = concaveHull.get(i);
            Assertions.assertEquals(expectedPoint.getX(), actualPoint.getX());
            Assertions.assertEquals(expectedPoint.getY(), actualPoint.getY());
        }
    }

}
