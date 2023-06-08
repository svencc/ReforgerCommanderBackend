package com.rcb.service.map.cluster;

import com.rcb.dto.map.cluster.ClusterDto;
import com.rcb.dto.map.cluster.ClusterPointDto;
import com.rcb.dto.map.cluster.ConvexHullDto;
import com.rcb.dto.map.cluster.VertexPointDto;
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
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClusteringService {

    @NonNull
    private final MapEntityPersistenceLayer mapEntityPersistenceLayer;

    private MonotoneChain hullGenerator;

    @PostConstruct
    public void postConstruct() {
        hullGenerator = new MonotoneChain();
    }

    @NonNull
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "MapEntityPersistenceLayer.generateClusters", key = "#mapName")
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

        final double eps_maximumRadiusOfTheNeighborhood = towns.size();
        final int minPts_minimumNumberOfPointsNeededForCluster = 6;

        final DBSCANClusterer<DoublePoint> dbscanClusterer = new DBSCANClusterer<>(
                eps_maximumRadiusOfTheNeighborhood,
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
    private static List<ClusterPointDto> toPointList(@NonNull final Cluster<DoublePoint> cluster) {
        return cluster.getPoints().stream()
                .<ClusterPointDto>map((final DoublePoint doublePoint) -> ClusterPointDto.builder()
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
        final List<VertexPointDto> verticeDtoList = toVertexPointDtoList(hullVertices);

        return ConvexHullDto.builder()
                .vertices(verticeDtoList)
                .build();
    }

    @NonNull
    private List<Vector2D> toVector2DList(@NonNull final Cluster<DoublePoint> cluster) {
        return cluster.getPoints().stream()
                .map((final DoublePoint doublePoint) -> new Vector2D(doublePoint.getPoint()))
                .toList();
    }

    @NonNull
    private static List<VertexPointDto> toVertexPointDtoList(@NonNull final Collection<Vector2D> hullVertices) {
        return hullVertices.stream()
                .<VertexPointDto>map((@NonNull final Vector2D vector2D) -> VertexPointDto.builder()
                        .x(BigDecimal.valueOf(vector2D.getX()))
                        .y(BigDecimal.valueOf(vector2D.getY()))
                        .build()
                )
                .toList();
    }

}
