package lib.main.convexhull;

import lib.javaconcavehull.main.concavehull.ConcaveHull;
import lib.javaconcavehull.main.concavehull.Point;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ConcaveHullTest {

    @Test
    public void testStackOverflowCase() {
        // prepare
        final Point[] stackOverflowCase = {
                new Point(1.01, 101.01),
                new Point(1.01, 101.5),
                new Point(1.5, 101.5),
                new Point(1.5, 101.01)
        };
        final List<Point> stackOutList = new ArrayList<>(Arrays.asList(stackOverflowCase));

        // execute
        final ConcaveHull c = new ConcaveHull();
        List<Point> concaveHullToTest = c.calculateConcaveHull(stackOutList, 1);

        // assert
        assertNotNull(concaveHullToTest);
        System.out.println("Hull test from GitHub");
        for (Point p : concaveHullToTest) {
            System.out.print(" " + p);
        }
    }

    @Test
    public void testMyTestCase() {
        // prepare
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

        // execute
        final ConcaveHull c = new ConcaveHull();
        final List<Point> concaveHullToTest = c.calculateConcaveHull(cluster, cluster.size()-1);

        // assert
        assertNotNull(concaveHullToTest);
        System.out.println("Hull test from me");
        for (Point p : concaveHullToTest) {
            System.out.print(" " + p);
        }

        List<Point> expected = new ArrayList<>(Arrays.asList(
                Point.builder().x(-5d).y(-3d).build(),
                Point.builder().x(-5d).y(-2d).build(),
                Point.builder().x(-5d).y(-1d).build(),
                Point.builder().x(-5d).y(0d).build(),
                Point.builder().x(-5d).y(1d).build(),
                Point.builder().x(-4d).y(-3d).build(),
                Point.builder().x(-3d).y(-3d).build(),
                Point.builder().x(-2d).y(-3d).build(),
                Point.builder().x(-1d).y(-3d).build(),
                Point.builder().x(0d).y(-3d).build(),
                Point.builder().x(1d).y(-3d).build(),
                Point.builder().x(2d).y(-3d).build(),
                Point.builder().x(3d).y(-3d).build(),
                Point.builder().x(4d).y(-3d).build(),
                Point.builder().x(4d).y(-2d).build(),
                Point.builder().x(4d).y(-1d).build(),
                Point.builder().x(4d).y(0d).build(),
                Point.builder().x(4d).y(1d).build(),
                Point.builder().x(-6d).y(-4d).build(),
                Point.builder().x(-6d).y(-3d).build(),
                Point.builder().x(-6d).y(-2d).build(),
                Point.builder().x(-6d).y(-1d).build(),
                Point.builder().x(-6d).y(0d).build(),
                Point.builder().x(-6d).y(1d).build(),
                Point.builder().x(-5d).y(-4d).build(),
                Point.builder().x(-4d).y(-4d).build(),
                Point.builder().x(-3d).y(-4d).build(),
                Point.builder().x(-2d).y(-4d).build(),
                Point.builder().x(-1d).y(-4d).build(),
                Point.builder().x(0d).y(-4d).build(),
                Point.builder().x(1d).y(-4d).build(),
                Point.builder().x(2d).y(-4d).build(),
                Point.builder().x(3d).y(-4d).build(),
                Point.builder().x(4d).y(-4d).build(),
                Point.builder().x(5d).y(-4d).build(),
                Point.builder().x(5d).y(-3d).build(),
                Point.builder().x(5d).y(-2d).build(),
                Point.builder().x(5d).y(-1d).build(),
                Point.builder().x(5d).y(0d).build(),
                Point.builder().x(5d).y(1d).build()
        ));


        assert (concaveHullToTest.containsAll(expected));
    }

//    @Test
//    public void jtsTestCase() {
//        // prepare
//        final GeometryFactory geometryFactory = new GeometryFactory();
//        final Geometry[] cluster = GeometryFactory.toPointArray(List.of(
//                geometryFactory.createPoint(new Coordinate(1d, 1d)),
//                geometryFactory.createPoint(new Coordinate(2d, 2d)),
//                geometryFactory.createPoint(new Coordinate(3d, 1d)),
//                geometryFactory.createPoint(new Coordinate(3d, 3d)),
//                geometryFactory.createPoint(new Coordinate(2d, 4d)),
//                geometryFactory.createPoint(new Coordinate(1d, 3d))
//        ));
//        GeometryCollection geometryCollection = new GeometryCollection(cluster, geometryFactory);
//
//        // execute
//        org.locationtech.jts.algorithm.hull.ConcaveHull concaveHullGenerator = new org.locationtech.jts.algorithm.hull.ConcaveHull(geometryCollection);
//        final Geometry hullToTest = concaveHullGenerator.getHull();
//        int numPoints = hullToTest.convexHull().getNumPoints();
//        // assert
//        assertNotNull(hullToTest);
//        hullToTest.getS
//        List<Point> expected = new ArrayList<>(Arrays.asList(
//                Point.builder().x(1d).y(1d).build(),
//                Point.builder().x(2d).y(2d).build(),
//                Point.builder().x(3d).y(1d).build(),
//                Point.builder().x(3d).y(3d).build(),
//                Point.builder().x(2d).y(4d).build(),
//                Point.builder().x(1d).y(3d).build()
//        ));
//
////        assert (concaveHullToTest.containsAll(expected));
//    }


}