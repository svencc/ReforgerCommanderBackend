package lib.javaconcavehull.main.concavehull;

import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.*;

/**
 * Heavily based on this source
 * ==> https://github.com/rksh/java-concave-hull/blob/master/main/clustering/ConcaveHull.java
 * than improved with lombok and extract inner static classes
 *
 * <p>
 * ConcaveHull.java - 18/07/22
 *
 * @author Udo Schlegel - Udo.3.Schlegel(at)uni-konstanz.de, rksh
 * @version 1.1
 * <p>
 * This is an implementation of the algorithm described by Adriano Moreira and Maribel Yasmina Santos:
 * CONCAVE HULL: A K-NEAREST NEIGHBOURS APPROACH FOR THE COMPUTATION OF THE REGION OCCUPIED BY A SET OF POINTS.
 * GRAPP 2007 - International Conference on Computer Graphics Theory and Applications; pp 61-68.
 * <p>
 * https://repositorium.sdum.uminho.pt/bitstream/1822/6429/1/ConcaveHull_ACM_MYS.pdf
 * <p>
 * With help from https://github.com/detlevn/QGIS-ConcaveHull-Plugin/blob/master/concavehull.py
 */
@NoArgsConstructor
public class ConcaveHull {

    @NonNull
    private Double euclideanDistance(
            @NonNull final Point pointA,
            @NonNull final Point pointB
    ) {
        return Math.sqrt(Math.pow(pointA.getX() - pointB.getX(), 2) + Math.pow(pointA.getY() - pointB.getY(), 2));
    }

    @NonNull
    private List<Point> kNearestNeighbors(
            @NonNull final List<Point> pointList,
            @NonNull final Point q,
            @NonNull final Integer k
    ) {
        final TreeSet<DistancedPoint> distanced = new TreeSet<DistancedPoint>(new Comparator<DistancedPoint>() {
            @Override
            public int compare(DistancedPoint o1, DistancedPoint o2) {
                if (o1.getDistance().equals(o2.getDistance())) {
                    if (o1.getPoint().getY().equals(o2.getPoint().getY())) {
                        return o2.getPoint().getX().compareTo(o1.getPoint().getX());
                    }
                    return o1.getPoint().getY().compareTo(o2.getPoint().getY());
                }
                return o1.getDistance().compareTo(o2.getDistance());
            }
        });

        for (final Point aPoint : pointList) {
            distanced.add(DistancedPoint.builder().point(aPoint).distance(euclideanDistance(q, aPoint)).build());
        }

        final List<Point> result = new ArrayList<>();
        for (final DistancedPoint value : distanced) {
            result.add(value.getPoint());
        }

        return result;
    }

    @NonNull
    private Point findMinYPoint(@NonNull final List<Point> pointList) {
        Collections.sort(pointList, new Comparator<Point>() {
            @Override
            public int compare(Point o1, Point o2) {
                if (o1.getY().equals(o2.getY())) {
                    if (o1.getX().equals(o2.getX())) {
                        //a tie should be impossible if we deduplicate the set
                    } else {
                        //break Y ties in X, following the "wrapping" direction
                        return o2.getX().compareTo(o1.getX());
                    }
                }
                return o1.getY().compareTo(o2.getY());
            }
        });

        return pointList.get(0);
    }

    @NonNull
    private Double calculateAngle(
            @NonNull final Point o1,
            @NonNull final Point o2
    ) {
        return Math.atan2(o2.getY() - o1.getY(), o2.getX() - o1.getX());
    }

    @NonNull
    private Double angleDifference(
            @NonNull final Double a1,
            @NonNull final Double a2
    ) {
        // calculate angle difference in clockwise directions as radians
        if ((a1 > 0 && a2 >= 0) && a1 > a2) {
            return Math.abs(a1 - a2);
        } else if ((a1 >= 0 && a2 > 0) && a1 < a2) {
            return 2 * Math.PI + a1 - a2;
        } else if ((a1 < 0 && a2 <= 0) && a1 < a2) {
            return 2 * Math.PI + a1 + Math.abs(a2);
        } else if ((a1 <= 0 && a2 < 0) && a1 > a2) {
            return Math.abs(a1 - a2);
        } else if (a1 <= 0 && 0 < a2) {
            return 2 * Math.PI + a1 - a2;
        } else if (a1 >= 0 && 0 >= a2) {
            return a1 + Math.abs(a2);
        } else {
            return 0.0;
        }
    }

    @NonNull
    private List<Point> sortByAngle(
            @NonNull final List<Point> pointList,
            @NonNull final Point q,
            @NonNull final Double a
    ) {
        // Sort by angle descending
        Collections.sort(pointList, new Comparator<Point>() {
            @Override
            public int compare(final Point o1, final Point o2) {
                Double a1 = angleDifference(a, calculateAngle(q, o1));
                Double a2 = angleDifference(a, calculateAngle(q, o2));
                return a2.compareTo(a1);
            }
        });

        return pointList;
    }

    @NonNull
    private Boolean intersect(
            @NonNull final Point l1p1,
            @NonNull final Point l1p2,
            @NonNull final Point l2p1,
            @NonNull final Point l2p2
    ) {
        // calculate part equations for line-line intersection
        final Double a1 = l1p2.getY() - l1p1.getY();
        final Double b1 = l1p1.getX() - l1p2.getX();
        final Double c1 = a1 * l1p1.getX() + b1 * l1p1.getY();
        final Double a2 = l2p2.getY() - l2p1.getY();
        final Double b2 = l2p1.getX() - l2p2.getX();
        final Double c2 = a2 * l2p1.getX() + b2 * l2p1.getY();
        // calculate the divisor
        final Double tmp = (a1 * b2 - a2 * b1);

        // calculate intersection point x coordinate
        final Double pX = (c1 * b2 - c2 * b1) / tmp;

        // check if intersection x coordinate lies in line line segment
        if ((pX > l1p1.getX() && pX > l1p2.getX()) || (pX > l2p1.getX() && pX > l2p2.getX())
                || (pX < l1p1.getX() && pX < l1p2.getX()) || (pX < l2p1.getX() && pX < l2p2.getX())) {
            return false;
        }

        // calculate intersection point y coordinate
        final Double pY = (a1 * c2 - a2 * c1) / tmp;

        // check if intersection y coordinate lies in line line segment
        if ((pY > l1p1.getY() && pY > l1p2.getY()) || (pY > l2p1.getY() && pY > l2p2.getY())
                || (pY < l1p1.getY() && pY < l1p2.getY()) || (pY < l2p1.getY() && pY < l2p2.getY())) {
            return false;
        }

        return true;
    }

    private boolean pointInPolygon(
            @NonNull final Point p,
            @NonNull final List<Point> pp
    ) {
        boolean result = false;
        for (int i = 0, j = pp.size() - 1; i < pp.size(); j = i++) {
            if ((pp.get(i).getY() > p.getY()) != (pp.get(j).getY() > p.getY()) &&
                    (p.getX() < (pp.get(j).getX() - pp.get(i).getX()) * (p.getY() - pp.get(i).getY()) / (pp.get(j).getY() - pp.get(i).getY()) + pp.get(i).getX())) {
                result = !result;
            }
        }

        return result;
    }

    @NonNull
    public List<Point> calculateConcaveHull(
            @NonNull final List<Point> pointArrayList,
            @NonNull final Integer k
    ) {
        // the resulting concave hull
        final List<Point> concaveHull = new ArrayList<>();

        // optional remove duplicates
        final HashSet<Point> set = new HashSet<>(pointArrayList);
        final List<Point> pointArraySet = new ArrayList<>(set);

        // k has to be greater than 3 to execute the algorithm
        Integer kk = Math.max(k, 3);

        // return Points if already Concave Hull
        if (pointArraySet.size() < 3) {
            return pointArraySet;
        }

        // make sure that k neighbors can be found
        kk = Math.min(kk, pointArraySet.size() - 1);

        // find first point and remove from point list
        final Point firstPoint = findMinYPoint(pointArraySet);
        concaveHull.add(firstPoint);
        Point currentPoint = firstPoint;
        pointArraySet.remove(firstPoint);

        Double previousAngle = 0.0;
        Integer step = 2;

        while ((currentPoint != firstPoint || step == 2) && pointArraySet.size() > 0) {

            // after 3 steps add first point to dataset, otherwise hull cannot be closed
            if (step == 5) {
                pointArraySet.add(firstPoint);
            }

            // get k nearest neighbors of current point
            final List<Point> kNearestPoints = kNearestNeighbors(pointArraySet, currentPoint, kk);

            // sort points by angle clockwise
            final List<Point> clockwisePoints = sortByAngle(kNearestPoints, currentPoint, previousAngle);

            // check if clockwise angle nearest neighbors are candidates for concave hull
            Boolean its = true;
            int i = -1;
            while (its && i < clockwisePoints.size() - 1) {
                i++;

                int lastPoint = 0;
                if (clockwisePoints.get(i) == firstPoint) {
                    lastPoint = 1;
                }

                // check if possible new concave hull point intersects with others
                int j = 2;
                its = false;
                while (!its && j < concaveHull.size() - lastPoint) {
                    its = intersect(concaveHull.get(step - 2), clockwisePoints.get(i), concaveHull.get(step - 2 - j), concaveHull.get(step - 1 - j));
                    j++;
                }
            }

            // if there is no candidate increase k - try again
            if (its) {
                // unless we've exhausted the points
                if (kk >= (pointArrayList.size() - 1)) {
                    return Collections.emptyList();
                }
                return calculateConcaveHull(pointArrayList, k + 1);
            }

            // add candidate to concave hull and remove from dataset
            currentPoint = clockwisePoints.get(i);
            concaveHull.add(currentPoint);
            pointArraySet.remove(currentPoint);

            // calculate last angle of the concave hull line
            previousAngle = calculateAngle(concaveHull.get(step - 1), concaveHull.get(step - 2));

            step++;
        }

        // Check if all points are contained in the concave hull
        Boolean insideCheck = true;
        int i = pointArraySet.size() - 1;

        while (insideCheck && i > 0) {
            if (kk >= (pointArrayList.size() - 1)) {
                return null;
            }
            insideCheck = pointInPolygon(pointArraySet.get(i), concaveHull);
            i--;
        }

        // if not all points inside -  try again
        if (!insideCheck) {
            return calculateConcaveHull(pointArrayList, k + 1);
        } else {
            return concaveHull;
        }
    }

}


