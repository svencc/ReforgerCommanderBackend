package com.rcb.service.map.cluster;

import com.rcb.dto.map.Line2DDto;
import com.rcb.dto.map.Point2DDto;
import com.rcb.dto.map.cluster.ClusterDto;
import com.rcb.dto.map.cluster.ConvexHullDto;
import com.rcb.dto.map.scanner.MapEntityDto;
import com.rcb.mapper.MapEntityMapper;
import com.rcb.repository.mapEntity.MapEntityPersistenceLayer;
import com.rcb.util.JSNumberUtil;
import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.geometry.euclidean.twod.hull.MonotoneChain;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClusteringService {

    @NonNull
    private final MapEntityPersistenceLayer mapEntityPersistenceLayer;

    private MonotoneChain hullGenerator;

    @NonNull
    private static List<Line2DDto> toLineList(@NonNull final Collection<Vector2D> hullVertices) {
        final List<Line2DDto> lines = new ArrayList<>();
        final AtomicReference<Point2DDto> startPoint = new AtomicReference<>();
        hullVertices.forEach((final Vector2D vector2D) -> {
            final Point2DDto currentPoint = Point2DDto.builder()
                    .x(BigDecimal.valueOf(vector2D.getX()))
                    .y(BigDecimal.valueOf(vector2D.getY()))
                    .build();

            if (startPoint.get() == null) {
                // first iteration: set starting point of line
                startPoint.set(currentPoint);
            } else {
                // close line: set endpoint
                lines.add(Line2DDto.builder()
                        .start(startPoint.get())
                        .end(currentPoint)
                        .build());

                // unset start point for next line:
                startPoint.set(currentPoint);
            }
        });

        // create last line to close convex hull!:
        lines.add(Line2DDto.builder()
                .start(lines.get(lines.size() - 1).getEnd())
                .end(lines.get(0).getStart())
                .build());

        return lines;
    }

    @PostConstruct
    public void postConstruct() {
        hullGenerator = new MonotoneChain();
    }

    @NonNull
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "MapEntityPersistenceLayer.generateClusters", key = "{#root.methodName, #mapName, #epsilon_maximumRadiusOfTheNeighborhood, #minPts_minimumNumberOfPointsNeededForCluster}")
    public List<ClusterDto> generateClusters(
            @NonNull final String mapName,
            final double epsilon_maximumRadiusOfTheNeighborhood,
            final int minPts_minimumNumberOfPointsNeededForCluster
    ) {
        final List<DoublePoint> buildings = mapEntityPersistenceLayer.findAllTownBuildingEntities(mapName).stream()
                .map(MapEntityMapper.INSTANCE::toDto)
                .map(MapEntityDto::getCoordinates)
                .filter(Objects::nonNull)
                .filter(vector -> vector.size() == 3)
                .map(vector -> {
                    final double[] point = {vector.get(0).doubleValue(), vector.get(2).doubleValue()};
                    return new DoublePoint(point);
                })
                .toList();

        final List<DoublePoint> towns = mapEntityPersistenceLayer.findAllTownEntities(mapName).stream()
                .map(MapEntityMapper.INSTANCE::toDto)
                .map(MapEntityDto::getCoordinates)
                .filter(Objects::nonNull)
                .filter(vector -> vector.size() == 3)
                .map(vector -> {
                    final double[] point = {vector.get(0).doubleValue(), vector.get(2).doubleValue()};
                    return new DoublePoint(point);
                })
                .toList();

        final DBSCANClusterer<DoublePoint> dbscanClusterer = new DBSCANClusterer<>(
                epsilon_maximumRadiusOfTheNeighborhood,
                minPts_minimumNumberOfPointsNeededForCluster
        );

        final List<Cluster<DoublePoint>> clusters = dbscanClusterer.cluster(buildings);
        final List<ClusterDto> clusterDtoList = clusters.stream()
                .map((final Cluster<DoublePoint> cluster) -> ClusterDto.builder()
                        .points(toPointList(cluster))
                        .convexHull(provideConvexHull(cluster))
                        .build())
                .toList();

        return clusterDtoList;
    }

    @NonNull
    private static List<Point2DDto> toPointList(@NonNull final Cluster<DoublePoint> cluster) {
        return cluster.getPoints().stream()
                .<Point2DDto>map((final DoublePoint doublePoint) -> Point2DDto.builder()
                        .x(JSNumberUtil.of(doublePoint.getPoint()[0]))
                        .y(JSNumberUtil.of(doublePoint.getPoint()[1]))
                        .build()
                )
                .toList();
    }

    @NonNull
    private ConvexHullDto provideConvexHull(@NonNull final Cluster<DoublePoint> cluster) {
        final List<Vector2D> vectorList = toVector2DList(cluster);
        final Collection<Vector2D> hullVertices = hullGenerator.findHullVertices(vectorList);
//        final List<Line2DDto> lines = toLineList(hullVertices);
        final List<Point2DDto> polygon = toPolygon(hullVertices);

        return ConvexHullDto.builder()
//                .lines(lines)
                .points(polygon)
                .build();
    }

    @NonNull
    private List<Vector2D> toVector2DList(@NonNull final Cluster<DoublePoint> cluster) {
        return cluster.getPoints().stream()
                .map((final DoublePoint doublePoint) -> new Vector2D(doublePoint.getPoint()))
                .toList();
    }

    @NonNull
    private static List<Point2DDto> toPolygon(@NonNull final Collection<Vector2D> hullVertices) {
        return hullVertices.stream()
                .map((final Vector2D vector2D) -> Point2DDto.builder()
                        .x(BigDecimal.valueOf(vector2D.getX()))
                        .y(BigDecimal.valueOf(vector2D.getY()))
                        .build())
                .collect(Collectors.toList());
    }

}
