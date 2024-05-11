package com.recom.commons.map.rasterizer.batch1;

import com.recom.commons.calculator.CoordinateConverter;
import com.recom.commons.calculator.d8algorithm.D8AlgorithmForStructureCluster;
import com.recom.commons.calculator.d8algorithm.D8AlgorithmForStructureClusterMap;
import com.recom.commons.map.MapComposer;
import com.recom.commons.map.rasterizer.batch0.StructureSpatialIndexCreator;
import com.recom.commons.map.rasterizer.configuration.BatchOrder;
import com.recom.commons.map.rasterizer.configuration.LayerOrder;
import com.recom.commons.map.rasterizer.configuration.MapLayerRasterizer;
import com.recom.commons.map.rasterizer.mapdesignscheme.MapDesignScheme;
import com.recom.commons.model.DEMDescriptor;
import com.recom.commons.model.maprendererpipeline.CreatedArtifact;
import com.recom.commons.model.maprendererpipeline.MapComposerWorkPackage;
import com.recom.commons.model.maprendererpipeline.MapLayerRasterizerConfiguration;
import com.recom.commons.model.maprendererpipeline.dataprovider.ClusterIndex;
import com.recom.commons.model.maprendererpipeline.dataprovider.SpacialIndex;
import com.recom.commons.model.maprendererpipeline.dataprovider.structure.StructureItem;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

@Slf4j
@Getter
@Setter
@RequiredArgsConstructor
public class StructureClusterRasterizer implements MapLayerRasterizer {

    @NonNull
    private final CoordinateConverter coordinateConverter = new CoordinateConverter();
    @NonNull
    private final MapComposer mapComposer;
    @NonNull
    private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();


    @NonNull
    private MapLayerRasterizerConfiguration mapLayerRasterizerConfiguration = MapLayerRasterizerConfiguration.builder()
            .rasterizerName(getClass().getSimpleName())
            .batch(BatchOrder.BASIC_BATCH)
            .layerOrder(LayerOrder.STRUCTURE_CLUSTER_MAP)
            .visible(false)
            .build();

    @NonNull
    private int[] rasterizeStructureClusterMap(
            @NonNull final DEMDescriptor demDescriptor,
            @NonNull final SpacialIndex<StructureItem> structureSpacialIndex,
            @NonNull final MapDesignScheme mapScheme
    ) {
        final D8AlgorithmForStructureCluster d8AlgorithmForStructureCluster = new D8AlgorithmForStructureCluster();
        final ClusterIndex<StructureItem> clusterIndex = d8AlgorithmForStructureCluster.generateStructureClusters(structureSpacialIndex);

        final D8AlgorithmForStructureClusterMap d8AlgorithmForStructureClusterMap = new D8AlgorithmForStructureClusterMap();
        final int[][] clusterMap = d8AlgorithmForStructureClusterMap.generateMap(demDescriptor, clusterIndex, mapScheme);

        final int width = demDescriptor.getDemWidth();
        final int height = demDescriptor.getDemHeight();

        final int[] pixelBuffer = new int[width * height];
        IntStream.range(0, width).parallel().forEach(demX -> {
            for (int demY = 0; demY < height; demY++) {
                pixelBuffer[demX + demY * width] = clusterMap[demX][demY];
            }
        });

        return pixelBuffer;
    }

    @Override
    public String getRasterizerName() {
        return getClass().getSimpleName();
    }

    @Override
    public void render(@NonNull final MapComposerWorkPackage workPackage) {
        workPackage.getPipelineArtifacts().getArtifacts().entrySet().stream()
                .filter(entry -> entry.getKey().equals(StructureSpatialIndexCreator.class))
                .findFirst()
                .ifPresent(entry -> {
                    final CreatedArtifact artifact = entry.getValue();
                    final SpacialIndex<StructureItem> spatialIndex = artifact.getData();

                    final int[] rawStructureMap = rasterizeStructureClusterMap(workPackage.getMapComposerConfiguration().getDemDescriptor(), spatialIndex, workPackage.getMapComposerConfiguration().getMapDesignScheme());
                    workPackage.getPipelineArtifacts().addArtifact(this, rawStructureMap);
                });
    }

}