package com.recom.service.map.cluster;

import com.recom.dto.map.Point2DDto;
import com.recom.dto.map.cluster.ClusterDto;
import com.recom.dto.map.cluster.ConcaveHullDto;
import com.recom.dto.map.cluster.ConvexHullDto;
import com.recom.dto.map.scanner.MapEntityDto;
import com.recom.mapper.MapEntityMapper;
import com.recom.model.map.ClusterConfiguration;
import com.recom.repository.mapEntity.MapEntityPersistenceLayer;
import com.recom.service.configuration.ConfigurationValueProvider;
import com.recom.util.JSNumberUtil;
import jakarta.annotation.PostConstruct;
import lib.javaconcavehull.main.alphashape.AlphaShapeConcaveHull;
import lib.javaconcavehull.main.concavehull.ConcaveHull;
import lib.javaconcavehull.main.concavehull.Point;
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

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClusteringService {

    @NonNull
    private final ConfigurationValueProvider configurationValueProvider;
    @NonNull
    private final MapEntityPersistenceLayer mapEntityPersistenceLayer;

    private MonotoneChain convexHullGenerator;
    private ConcaveHull concaveHullGenerator;

    @NonNull
    private static List<Point2DDto> toPoint2DList(@NonNull final Cluster<DoublePoint> cluster) {
        return cluster.getPoints().stream()
                .<Point2DDto>map((final DoublePoint doublePoint) -> Point2DDto.builder()
                        .x(JSNumberUtil.of(doublePoint.getPoint()[0]))
                        .y(JSNumberUtil.of(doublePoint.getPoint()[1]))
                        .build()
                )
                .toList();
    }

    @PostConstruct
    public void postConstruct() {
        convexHullGenerator = new MonotoneChain();
        concaveHullGenerator = new ConcaveHull();
    }

    @NonNull
    @Cacheable(cacheNames = "MapEntityPersistenceLayer.generateClusters")
    public List<ClusterDto> generateClusters(
            @NonNull final String mapName,
            @NonNull final List<ClusterConfiguration> clusterConfigurations
    ) {
        return clusterConfigurations.stream()
                .flatMap((final ClusterConfiguration configuration) -> {
                    final List<String> clusterResources = configurationValueProvider.queryValue(mapName, configuration.getClusteringResourcesListDescriptor());
                    final List<DoublePoint> resources = mapEntityPersistenceLayer.findAllByMapNameAndResourceNameIn(mapName, clusterResources).stream()
                            .map(MapEntityMapper.INSTANCE::toDto)
                            .map(MapEntityDto::getCoordinates)
                            .filter(Objects::nonNull)
                            .filter(vector -> vector.size() == 3)
                            .map(this::vectorToPoint)
                            .toList();

                    final DBSCANClusterer<DoublePoint> dbscanClusterer = new DBSCANClusterer<>(
                            configurationValueProvider.queryValue(mapName, configuration.getDbscanClusteringEpsilonMaximumRadiusOfTheNeighborhoodDescriptor()),
                            configurationValueProvider.queryValue(mapName, configuration.getDbscanClusteringVillageMinimumPointsDescriptor())
                    );

                    return dbscanClusterer.cluster(resources).stream()
                            .map((final Cluster<DoublePoint> cluster) -> ClusterDto.builder()
//                                    .points(toPoint2DList(cluster))
                                    .convexHull(provideConvexHull(cluster))
//                                    .concaveHull(provideConcaveHull(cluster))
                                    .build());
                })
                .collect(Collectors.toList());
    }

    @NonNull
    private DoublePoint vectorToPoint(@NonNull final List<BigDecimal> vector) {
        final double[] point = {vector.get(0).doubleValue(), vector.get(2).doubleValue()};

        return new DoublePoint(point);
    }

    @NonNull
    private ConvexHullDto provideConvexHull(@NonNull final Cluster<DoublePoint> cluster) {
        final List<Vector2D> vectorList = toVector2DList(cluster);
        final Collection<Vector2D> hullVertices = convexHullGenerator.findHullVertices(vectorList);
        final List<Point2DDto> vertices = toPolygon(hullVertices);
        final List<Point2DDto> reducedVertices = reduce(vertices);

        // @TODO: Set vertices empty if lower than 3 edges!
        return ConvexHullDto.builder()
                .vertices(reducedVertices)
                .build();
    }

    @NonNull
    private ConcaveHullDto provideConcaveHull(@NonNull final Cluster<DoublePoint> cluster) {
        final List<Point> vectorList = toPointList(cluster);
//        final List<Point> hullVertices = concaveHullGenerator.calculateConcaveHull(vectorList, (vectorList.size() - 2));
//        final List<Point> hullVertices = concaveHullGenerator.calculateConcaveHull(vectorList, (vectorList.size() - 2));

        final List<Point> hullVertices = AlphaShapeConcaveHull.computeConcaveHull(vectorList, 500);

        final List<Point2DDto> vertices = toPolygon(hullVertices);
        final List<Point2DDto> reducedVertices = reduce(vertices);

        // @TODO: Set vertices empty if lower than 3 edges!
        return ConcaveHullDto.builder()
                .vertices(reducedVertices)
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

    @NonNull
    private List<Point2DDto> reduce(@NonNull final List<Point2DDto> vertices) {
        final double roundingFactor = 50; // Abrunden auf 10 Meter

        return vertices.stream()
                .peek((final Point2DDto point2D) -> {
                    double roundedX = Math.round(point2D.getX().doubleValue() / roundingFactor) * roundingFactor;
                    double roundedY = Math.round(point2D.getY().doubleValue() / roundingFactor) * roundingFactor;
                    point2D.setX(BigDecimal.valueOf(roundedX));
                    point2D.setY(BigDecimal.valueOf(roundedY));
                })
                .distinct()
                .toList();
    }

    @NonNull
    private List<Point> toPointList(@NonNull final Cluster<DoublePoint> cluster) {
        return cluster.getPoints().stream()
                .map((point) -> Point.builder()
                        .x(point.getPoint()[0])
                        .y(point.getPoint()[1])
                        .build()
                )
                .collect(Collectors.toList());
    }

    @NonNull
    private List<Point2DDto> toPolygon(@NonNull final List<Point> hullVertices) {
        return hullVertices.stream()
                .map((final Point point) -> Point2DDto.builder()
                        .x(BigDecimal.valueOf(point.getX()))
                        .y(BigDecimal.valueOf(point.getY()))
                        .build())
                .collect(Collectors.toList());
    }

}
