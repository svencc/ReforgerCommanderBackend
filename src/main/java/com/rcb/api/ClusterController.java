package com.rcb.api;

import com.rcb.api.commons.HttpCommons;
import com.rcb.dto.map.cluster.ClusterDto;
import com.rcb.dto.map.cluster.ClusterListDto;
import com.rcb.dto.map.cluster.ClusterPointDto;
import com.rcb.dto.map.scanner.MapEntityDto;
import com.rcb.mapper.MapEntityMapper;
import com.rcb.repository.mapEntity.MapEntityPersistenceLayer;
import com.rcb.util.JSNumberUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@Tag(name = "MapScanner")
@RequiredArgsConstructor
@RequestMapping("/api/v1/clustering")
public class ClusterController {

    private final MapEntityPersistenceLayer mapEntityPersistenceLayer;

    @Operation(
            summary = "Starts DBSCAN clustering",
            description = "test only."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = HttpCommons.OK_CODE, description = HttpCommons.OK)
    })
    @PostMapping(path = "start", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ClusterListDto> testClustering() {
        log.debug("Requested POST /api/v1/clustering/start");

        final List<DoublePoint> buildings = mapEntityPersistenceLayer.findAllBuildings("$ReforgerCommanderClient:worlds/Everon.ent").stream()
                .map(MapEntityMapper.INSTANCE::toDto)
                .map(MapEntityDto::getCoordinates)
                .filter(Objects::nonNull)
                .filter(vector -> vector.size() == 3)
                .map(vector -> {
                    final double[] point = {vector.get(0).doubleValue(), vector.get(2).doubleValue()};
                    return new DoublePoint(point);
                })
                .toList();

        final List<DoublePoint> towns = mapEntityPersistenceLayer.findAllTowns("$ReforgerCommanderClient:worlds/Everon.ent").stream()
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
                .map(cluster -> ClusterDto.builder()
                        .points(cluster.getPoints().stream()
                                .map(point -> ClusterPointDto.builder()
                                        .x(JSNumberUtil.of(point.getPoint()[0]))
                                        .y(JSNumberUtil.of(point.getPoint()[1]))
                                        .build()
                                )
                                .toList()
                        )
                        .build())
                .toList();

        return ResponseEntity.status(HttpStatus.OK)
                .cacheControl(CacheControl.noCache())
                .body(ClusterListDto.builder()
                        .clusterList(clusterDtoList)
                        .build());
    }

}
