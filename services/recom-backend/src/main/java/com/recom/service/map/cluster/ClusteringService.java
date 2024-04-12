package com.recom.service.map.cluster;

import com.recom.dto.map.Point2DDto;
import com.recom.dto.map.cluster.ClusterDto;
import com.recom.dto.map.cluster.ClusterResponseDto;
import com.recom.dto.map.cluster.ConcaveHullDto;
import com.recom.dto.map.cluster.ConvexHullDto;
import com.recom.dto.map.scanner.structure.MapStructureDto;
import com.recom.entity.map.GameMap;
import com.recom.mapper.mapstructure.MapStructureEntityMapper;
import com.recom.model.map.ClusterConfiguration;
import com.recom.persistence.map.GameMapPersistenceLayer;
import com.recom.persistence.map.structure.MapStructurePersistenceLayer;
import com.recom.service.configuration.ConfigurationDescriptorProvider;
import com.recom.service.configuration.ConfigurationValueProvider;
import com.recom.service.cache.dbcached.DBCachedService;
import com.recom.util.JSNumberUtil;
import jakarta.annotation.PostConstruct;
import lib.clipboard.alphashape.AlphaShapeConcaveHull;
import lib.clipboard.concavehull.ConcaveHull;
import lib.clipboard.concavehull.Point;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClusteringService {

    @NonNull
    public static final String CLUSTERINGSERVICE_GENERATECLUSTERS_CACHE = "ClusteringService.generateClusters";

    @NonNull
    private final ConfigurationValueProvider configurationValueProvider;
    @NonNull
    private final MapStructurePersistenceLayer mapStructurePersistenceLayer;
    @NonNull
    private final GameMapPersistenceLayer gameMapPersistenceLayer;
    @NonNull
    private final DBCachedService dbCachedService;

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

    @Cacheable(cacheNames = CLUSTERINGSERVICE_GENERATECLUSTERS_CACHE)
    public ClusterResponseDto generateClusters(@NonNull final GameMap gameMap) {
        final List<ClusterConfiguration> clusterConfigurations = List.of(
                ClusterConfiguration.builder()
                        .clusteringResourcesListDescriptor(ConfigurationDescriptorProvider.CLUSTERING_VILLAGE_RESOURCES_LIST)
                        .dbscanClusteringEpsilonMaximumRadiusOfTheNeighborhoodDescriptor(ConfigurationDescriptorProvider.CLUSTERING_VILLAGE_EPSILON_MAXIMUM_RADIUS_OF_THE_NEIGHBORHOOD)
                        .dbscanClusteringVillageMinimumPointsDescriptor(ConfigurationDescriptorProvider.CLUSTERING_VILLAGE_MINIMUM_POINTS)
                        .build()
//                ClusterConfiguration.builder()
//                        .clusteringResourcesListDescriptor(ConfigurationDescriptorProvider.CLUSTERING_FOREST_RESOURCES_LIST)
//                        .dbscanClusteringEpsilonMaximumRadiusOfTheNeighborhoodDescriptor(ConfigurationDescriptorProvider.CLUSTERING_FOREST_EPSILON_MAXIMUM_RADIUS_OF_THE_NEIGHBORHOOD)
//                        .dbscanClusteringVillageMinimumPointsDescriptor(ConfigurationDescriptorProvider.CLUSTERING_FOREST_MINIMUM_POINTS)
//                        .build(),
//                ClusterConfiguration.builder()
//                        .clusteringResourcesListDescriptor(ConfigurationDescriptorProvider.CLUSTERING_MILITARY_RESOURCES_LIST)
//                        .dbscanClusteringEpsilonMaximumRadiusOfTheNeighborhoodDescriptor(ConfigurationDescriptorProvider.CLUSTERING_MILITARY_EPSILON_MAXIMUM_RADIUS_OF_THE_NEIGHBORHOOD)
//                        .dbscanClusteringVillageMinimumPointsDescriptor(ConfigurationDescriptorProvider.CLUSTERING_MILITARY_MINIMUM_POINTS)
//                        .build()
        );
        return dbCachedService.proxyToDBCacheSafe(
                CLUSTERINGSERVICE_GENERATECLUSTERS_CACHE,
                gameMap.getName(),
                () -> {
                    ArrayList<ClusterDto> clusterList = clusterConfigurations.stream()
                            .flatMap((final ClusterConfiguration configuration) -> {
                                final List<String> clusterResources = configurationValueProvider.queryValue(gameMap, configuration.getClusteringResourcesListDescriptor());
                                final List<DoublePoint> resources = mapStructurePersistenceLayer.findAllByMapNameAndResourceNameIn(gameMap, clusterResources).stream()
                                        .map(MapStructureEntityMapper.INSTANCE::toDto)
                                        .map(MapStructureDto::getCoordinates)
                                        .filter(Objects::nonNull)
                                        .filter(vector -> vector.size() == 3)
                                        .map(this::vectorToPoint)
                                        .toList();

                                final DBSCANClusterer<DoublePoint> dbscanClusterer = new DBSCANClusterer<>(
                                        configurationValueProvider.queryValue(gameMap, configuration.getDbscanClusteringEpsilonMaximumRadiusOfTheNeighborhoodDescriptor()),
                                        configurationValueProvider.queryValue(gameMap, configuration.getDbscanClusteringVillageMinimumPointsDescriptor())
                                );

                                return dbscanClusterer.cluster(resources).stream()
                                        .map((final Cluster<DoublePoint> cluster) -> ClusterDto.builder()
//                                    .points(toPoint2DList(cluster))
                                                .convexHull(provideConvexHull(cluster))
//                                    .concaveHull(provideConcaveHull(cluster))
                                                .build());
                            })
                            .collect(Collectors.toCollection(ArrayList::new));

                    return ClusterResponseDto.builder()
                            .clusterList(clusterList)
                            .build();
                }
        );
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
        final double roundingFactor = 10; // Abrunden auf 10 Meter

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
