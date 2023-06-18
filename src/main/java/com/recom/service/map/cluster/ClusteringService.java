package com.recom.service.map.cluster;

import com.recom.dto.map.Point2DDto;
import com.recom.dto.map.cluster.ClusterDto;
import com.recom.dto.map.cluster.ConcaveHullDto;
import com.recom.dto.map.cluster.ConvexHullDto;
import com.recom.dto.map.scanner.MapEntityDto;
import com.recom.mapper.MapEntityMapper;
import com.recom.model.configuration.configurationvaluedescriptor.BaseRegisteredConfigurationValueDescripable;
import com.recom.repository.mapEntity.MapEntityPersistenceLayer;
import com.recom.service.configuration.DefaultConfigurationProvidable;
import com.recom.service.configuration.ConfigurationValueProvider;
import com.recom.service.configuration.DefaultConfigurationDatabaseInitializer;
import com.recom.util.JSNumberUtil;
import jakarta.annotation.PostConstruct;
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
import java.math.RoundingMode;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClusteringService implements DefaultConfigurationProvidable {

    @NonNull
    private final DefaultConfigurationDatabaseInitializer defaultConfigurationDatabaseInitializer;
    @NonNull
    private final ConfigurationValueProvider configurationValueProvider;
    @NonNull
    private final MapEntityPersistenceLayer mapEntityPersistenceLayer;

    private MonotoneChain convexHullGenerator;
    private ConcaveHull concaveHullGenerator;


    @PostConstruct
    public void postConstruct() {
        convexHullGenerator = new MonotoneChain();
        concaveHullGenerator = new ConcaveHull();
        defaultConfigurationDatabaseInitializer.registerDefaultConfigurationProvider(this);
    }

    @NonNull
    @Cacheable(cacheNames = "MapEntityPersistenceLayer.generateClusters", key = "{#root.methodName, #mapName, #epsilon_maximumRadiusOfTheNeighborhood, #minPts_minimumNumberOfPointsNeededForCluster}")
    public List<ClusterDto> generateClusters(@NonNull final String mapName) {
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
                configurationValueProvider.queryValue(mapName, ClusterConfigurationDescriptors.EPSILON_MAXIMUM_RADIUS_OF_THE_NEIGHBORHOOD),
                configurationValueProvider.queryValue(mapName, ClusterConfigurationDescriptors.MINIMUM_NUMBER_OF_POINTS_NEEDED_FOR_CLUSTER)
        );

        final List<Cluster<DoublePoint>> clusters = dbscanClusterer.cluster(buildings);
        final List<ClusterDto> clusterDtoList = clusters.stream()
                .map((final Cluster<DoublePoint> cluster) -> ClusterDto.builder()
                        .points(toPoint2DList(cluster))
                        .convexHull(provideConvexHull(cluster))
                        .concaveHull(provideConcaveHull(cluster))
                        .build())
                .toList();

        return clusterDtoList;
    }

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

    private ConcaveHullDto provideConcaveHull(@NonNull final Cluster<DoublePoint> cluster) {
        final List<Point> vectorList = toPointList(cluster);
        final List<Point> hullVertices = concaveHullGenerator.calculateConcaveHull(vectorList, (vectorList.size() - 2));
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
        return vertices.stream()
                .peek((final Point2DDto point2D) -> {
                    point2D.setX(point2D.getX().setScale(0, RoundingMode.DOWN).setScale(1));
                    point2D.setY(point2D.getY().setScale(0, RoundingMode.DOWN).setScale(1));
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
                .toList();
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

    @Override
    public @NonNull List<BaseRegisteredConfigurationValueDescripable> provideDefaultConfigurationValues() {
        return List.of(
                ClusterConfigurationDescriptors.EPSILON_MAXIMUM_RADIUS_OF_THE_NEIGHBORHOOD                ,
                ClusterConfigurationDescriptors.MINIMUM_NUMBER_OF_POINTS_NEEDED_FOR_CLUSTER
        );
    }

}
