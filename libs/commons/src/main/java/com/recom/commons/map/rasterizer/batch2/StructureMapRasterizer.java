package com.recom.commons.map.rasterizer.batch2;

import com.recom.commons.calculator.CoordinateConverter;
import com.recom.commons.calculator.d8algorithm.structure.D8AlgorithmForStructureMap;
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
import com.recom.commons.model.maprendererpipeline.dataprovider.structure.StructureItem;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

@Slf4j
@Getter
@Setter
@RequiredArgsConstructor
public class StructureMapRasterizer implements MapLayerRasterizer<int[]> {

    @NonNull
    private final CoordinateConverter coordinateConverter = new CoordinateConverter();
    @NonNull
    private final MapComposer mapComposer;
    @NonNull
    private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
    @NonNull
    private Optional<CompletableFuture<List<StructureItem>>> maybePreparationTask = Optional.empty();


    @NonNull
    private MapLayerRasterizerConfiguration mapLayerRasterizerConfiguration = MapLayerRasterizerConfiguration.builder()
            .rasterizerName(getClass().getSimpleName())
            .batch(BatchOrder.BASIC_BATCH)
            .layerOrder(LayerOrder.STRUCTURE_MAP)
            .visible(false)
            .build();

    @NonNull
    private int[] rasterizeStructureMap(
            @NonNull final DEMDescriptor demDescriptor,
            @NonNull final Cluster<StructureItem> structureCluster,
            @NonNull final MapDesignScheme mapScheme
    ) {
        final D8AlgorithmForStructureMap d8AlgorithmForStructureMap = new D8AlgorithmForStructureMap();
        final int[][] structureMap = d8AlgorithmForStructureMap.generateStructureMap(demDescriptor, structureCluster, mapScheme);

        final int width = demDescriptor.getDemWidth();
        final int height = demDescriptor.getDemHeight();
        final int[] pixelBuffer = new int[width * height];
        IntStream.range(0, width).parallel().forEach(demX -> {
            for (int demY = 0; demY < height; demY++) {
                pixelBuffer[demX + demY * width] = structureMap[demX][demY];
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
                .filter(entry -> entry.getKey().equals(StructureClusterCreator.class))
                .findFirst()
                .ifPresentOrElse(
                        (entry) -> {
                            final CreatedArtifact<Cluster<StructureItem>> artifact = entry.getValue();
                            final Cluster<StructureItem> structureCluster = artifact.getData();

                            final int[] rawStructureMap = rasterizeStructureMap(workPackage.getMapComposerConfiguration().getDemDescriptor(), structureCluster, workPackage.getMapComposerConfiguration().getMapDesignScheme());
                            workPackage.getPipelineArtifacts().addArtifact(this, rawStructureMap);
                        },
                        () -> log.error("StructureSpatialIndexCreator was not found in pipeline artifacts!")
                );
    }

    @NonNull
    @Override
    public Optional<int[]> findMyArtefactFromWorkPackage(@NonNull final MapComposerWorkPackage workPackage) {
        return workPackage.getPipelineArtifacts().getArtifactFrom(getClass()).map(CreatedArtifact::getData);
    }

}