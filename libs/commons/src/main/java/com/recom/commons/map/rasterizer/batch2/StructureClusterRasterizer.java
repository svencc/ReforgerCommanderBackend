package com.recom.commons.map.rasterizer.batch2;

import com.recom.commons.calculator.CoordinateConverter;
import com.recom.commons.calculator.d8algorithm.structure.D8AlgorithmForStructureCluster;
import com.recom.commons.calculator.d8algorithm.structure.D8AlgorithmForStructureClusterMap;
import com.recom.commons.map.MapComposer;
import com.recom.commons.map.rasterizer.batch0.StructureClusterCreator;
import com.recom.commons.map.rasterizer.configuration.BatchOrder;
import com.recom.commons.map.rasterizer.configuration.LayerOrder;
import com.recom.commons.map.rasterizer.configuration.MapLayerRasterizer;
import com.recom.commons.map.rasterizer.mapdesignscheme.MapDesignScheme;
import com.recom.commons.model.DEMDescriptor;
import com.recom.commons.model.maprendererpipeline.CreatedArtifact;
import com.recom.commons.model.maprendererpipeline.MapComposerWorkPackage;
import com.recom.commons.model.maprendererpipeline.MapLayerRasterizerConfiguration;
import com.recom.commons.model.maprendererpipeline.dataprovider.Cluster;
import com.recom.commons.model.maprendererpipeline.dataprovider.SpacialIndex;
import com.recom.commons.model.maprendererpipeline.dataprovider.structure.StructureItem;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

@Slf4j
@Getter
@Setter
@RequiredArgsConstructor
public class StructureClusterRasterizer implements MapLayerRasterizer<int[]> {

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
            @NonNull final Cluster<StructureItem> structureCluster,
            @NonNull final MapDesignScheme mapScheme
    ) {
        final D8AlgorithmForStructureCluster d8AlgorithmForStructureCluster = new D8AlgorithmForStructureCluster();
        final Cluster<StructureItem> cluster = d8AlgorithmForStructureCluster.generateStructureClusters(structureCluster);

        final D8AlgorithmForStructureClusterMap d8AlgorithmForStructureClusterMap = new D8AlgorithmForStructureClusterMap();
        final int[][] clusterMap = d8AlgorithmForStructureClusterMap.generateMap(demDescriptor, cluster, mapScheme);

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
        //@TODO <<<<<<<<<<<<<<<<<< still some work todo
        workPackage.getPipelineArtifacts().getArtifacts().entrySet().stream()
                .filter(entry -> entry.getKey().equals(StructureClusterCreator.class))
                .findFirst()
                .ifPresent(entry -> {
                    final CreatedArtifact<Cluster<StructureItem>> artifact = entry.getValue();
                    final Cluster<StructureItem> structureCluster = artifact.getData();

                    final int[] rawStructureMap = rasterizeStructureClusterMap(workPackage.getMapComposerConfiguration().getDemDescriptor(), structureCluster, workPackage.getMapComposerConfiguration().getMapDesignScheme());
                    workPackage.getPipelineArtifacts().addArtifact(this, rawStructureMap);
                });
    }

    @NonNull
    @Override
    public Optional<int[]> findMyArtefactFromWorkPackage(@NonNull final MapComposerWorkPackage workPackage) {
        return workPackage.getPipelineArtifacts().getArtifactFrom(getClass()).map(CreatedArtifact::getData);
    }

}