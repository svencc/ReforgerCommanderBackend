package com.recom.commons.map.rasterizer;

import com.recom.commons.calculator.CoordinateConverter;
import com.recom.commons.map.MapComposer;
import com.recom.commons.map.rasterizer.configuration.LayerOrder;
import com.recom.commons.map.rasterizer.configuration.MapLayerRasterizer;
import com.recom.commons.map.rasterizer.mapdesignscheme.MapDesignScheme;
import com.recom.commons.model.DEMDescriptor;
import com.recom.commons.model.maprendererpipeline.CreatedArtifact;
import com.recom.commons.model.maprendererpipeline.MapComposerWorkPackage;
import com.recom.commons.model.maprendererpipeline.MapLayerRasterizerConfiguration;
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
public class StructureClusterMapRasterizer implements MapLayerRasterizer {

    @NonNull
    private final CoordinateConverter coordinateConverter = new CoordinateConverter();
    @NonNull
    private final MapComposer mapComposer;
    @NonNull
    private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();


    @NonNull
    private MapLayerRasterizerConfiguration mapLayerRasterizerConfiguration = MapLayerRasterizerConfiguration.builder()
            .rasterizerName(getClass().getSimpleName())
            .layerOrder(LayerOrder.STRUCTURE_CLUSTER_MAP)
            .batch(1)
            .visible(false)
            .build();

    @NonNull
    private int[] rasterizeStructureClusterMap(
            @NonNull final DEMDescriptor demDescriptor,
            @NonNull final int[] structureMap,
            @NonNull final MapDesignScheme mapScheme
    ) {
        final int width = demDescriptor.getDemWidth();
        final int height = demDescriptor.getDemHeight();

//        final D8AlgorithmForStructureClusterMap d8AlgorithmForStructureClusterMap = new D8AlgorithmForStructureClusterMap(widht, height);
//        final int[][] structureClusterMap = d8AlgorithmForStructureClusterMap.generateStructureClusterMap(demDescriptor, spacialIndex, mapScheme);
        final int[][] structureClusterMap = new int[1][1];
        // @TODO <<<<<<<<<<<<<<<<<<< implementation goes here!!! <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

        final int[] pixelBuffer = new int[width * height];
        IntStream.range(0, width).parallel().forEach(demX -> {
            for (int demY = 0; demY < height; demY++) {
                pixelBuffer[demX + demY * width] = structureClusterMap[demX][demY];
            }
        });

        return pixelBuffer;
    }

    @Override
    public String getRasterizerName() {
        return getClass().getSimpleName();
    }

    @Override
    public void prepareAsync(@NonNull final MapComposerWorkPackage workPackage) {
        // runs AFTER StructureMapRasterizer, so we can use the structure provider cached result from the previous rasterizer
    }

    @Override
    public void render(@NonNull final MapComposerWorkPackage workPackage) {
        workPackage.getPipelineArtifacts().getArtifacts().entrySet().stream()
                .filter(entry -> entry.getKey().equals(StructureMapRasterizer.class))
                .findFirst()
                .ifPresent(entry -> {
                    final CreatedArtifact artifact = entry.getValue();
                    final int[] preparedStructureMap = artifact.getData();
                    final int[] rawStructureMap = rasterizeStructureClusterMap(workPackage.getMapComposerConfiguration().getDemDescriptor(), preparedStructureMap, workPackage.getMapComposerConfiguration().getMapDesignScheme());
                    workPackage.getPipelineArtifacts().addArtifact(this, rawStructureMap);
                });
    }

}