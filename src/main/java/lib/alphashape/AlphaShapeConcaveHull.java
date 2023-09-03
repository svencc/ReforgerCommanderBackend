package lib.alphashape;

import lib.concavehull.Point;
import lombok.NonNull;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class AlphaShapeConcaveHull {

    // Compute the concave hull using Alpha Shape algorithm
    public static List<Point> computeConcaveHull(List<Point> points, double alpha) {
        // Compute the convex hull using QuickHull
        final List<Point> convexHull = computeConvexHull(points);

        // Filter the convex hull to obtain the concave hull
        final List<Point> concaveHull = new ArrayList<>();
        for (int i = 0; i < convexHull.size(); i++) {
            Point p1 = convexHull.get(i);
            Point p2 = convexHull.get((i + 1) % convexHull.size());
            if (isEdgeAlphaValid(points, p1, p2, alpha)) {
                concaveHull.add(p1);
                concaveHull.add(p2);
            }
        }

        return concaveHull;
    }

    // Compute the convex hull using QuickHull
    private static List<Point> computeConvexHull(List<Point> points) {
        return QuickHull.quickHull(points);
//        final MonotoneChain concaveHullGenerator = new MonotoneChain();
//        return toPolygon(concaveHullGenerator.findHullVertices(toVector2DList(points)));
    }

    // Check if an edge of the convex hull is valid based on alpha value
    private static boolean isEdgeAlphaValid(List<Point> points, Point p1, Point p2, double alpha) {
        double distanceThreshold = computeDistanceThreshold(points, alpha);

        for (Point point : points) {
            if (point != p1 && point != p2) {
                double distance = computeDistanceToLineSegment(point, p1, p2);
                if (distance < distanceThreshold) {
                    return false;
                }
            }
        }

        return true;
    }

    @NonNull
    private static List<Point> toPolygon(@NonNull final Collection<Vector2D> hullVertices) {
        return hullVertices.stream()
                .map((final Vector2D vector2D) -> Point.builder()
                        .x(vector2D.getX())
                        .y(vector2D.getY())
                        .build())
                .collect(Collectors.toList());
    }

    @NonNull
    private static List<Vector2D> toVector2DList(@NonNull final List<Point> cluster) {
        return cluster.stream()
                .map((point) -> {
                    double[] doubles = {point.getX(), point.getY()};
                    return new Vector2D(doubles);
                })
                .toList();
    }

    // Compute the distance threshold based on alpha value
    private static double computeDistanceThreshold(List<Point> points, double alpha) {
        double minX = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;

        for (Point point : points) {
            minX = Math.min(minX, point.getX());
            maxX = Math.max(maxX, point.getX());
            minY = Math.min(minY, point.getY());
            maxY = Math.max(maxY, point.getY());
        }

        double diagonal = Math.sqrt(Math.pow(maxX - minX, 2) + Math.pow(maxY - minY, 2));
        return diagonal * alpha;
    }

    // Compute the distance from a point to a line segment
    private static double computeDistanceToLineSegment(Point point, Point p1, Point p2) {
        double lengthSquared = Math.pow(p2.getX() - p1.getX(), 2) + Math.pow(p2.getY() - p1.getY(), 2);
        if (lengthSquared == 0) {
            return computeDistance(point, p1);
        }

        double t = ((point.getX() - p1.getX()) * (p2.getX() - p1.getX()) + (point.getY() - p1.getY()) * (p2.getY() - p1.getY())) / lengthSquared;
        t = Math.max(0, Math.min(1, t));

        double projectionX = p1.getX() + t * (p2.getX() - p1.getX());
        double projectionY = p1.getY() + t * (p2.getY() - p1.getY());

        return computeDistance(point, new Point(projectionX, projectionY));
    }

    // Compute the distance between two points
    private static double computeDistance(Point p1, Point p2) {
        return Math.sqrt(Math.pow(p2.getX() - p1.getX(), 2) + Math.pow(p2.getY() - p1.getY(), 2));
    }
}
