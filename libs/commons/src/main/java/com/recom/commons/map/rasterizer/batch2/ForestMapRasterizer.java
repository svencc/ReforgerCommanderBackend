package com.recom.commons.map.rasterizer.batch2;

import com.recom.commons.calculator.CoordinateConverter;
import com.recom.commons.calculator.d8algorithm.forest.D8AlgorithmForForestMap;
import com.recom.commons.map.MapComposer;
import com.recom.commons.map.rasterizer.batch0.CreatorForestData;
import com.recom.commons.map.rasterizer.configuration.BatchOrder;
import com.recom.commons.map.rasterizer.configuration.LayerOrder;
import com.recom.commons.map.rasterizer.configuration.MapLayerRasterizer;
import com.recom.commons.map.rasterizer.mapdesignscheme.MapDesignScheme;
import com.recom.commons.model.DEMDescriptor;
import com.recom.commons.model.maprendererpipeline.CreatedArtifact;
import com.recom.commons.model.maprendererpipeline.MapComposerWorkPackage;
import com.recom.commons.model.maprendererpipeline.MapLayerRasterizerConfiguration;
import com.recom.commons.model.maprendererpipeline.dataprovider.Cluster;
import com.recom.commons.model.maprendererpipeline.dataprovider.forest.ForestItem;
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
public class ForestMapRasterizer implements MapLayerRasterizer<int[]> {

    @NonNull
    private final CoordinateConverter coordinateConverter = new CoordinateConverter();
    @NonNull
    private final MapComposer mapComposer;
    @NonNull
    private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
    @NonNull
    private Optional<CompletableFuture<List<ForestItem>>> maybePreparationTask = Optional.empty();

    @NonNull
    private MapLayerRasterizerConfiguration mapLayerRasterizerConfiguration = MapLayerRasterizerConfiguration.builder()
            .rasterizerName(getClass().getSimpleName())
            .batch(BatchOrder.BATCH_2)
            .layerOrder(LayerOrder.FOREST_MAP)
            .visible(false)
            .build();

    @NonNull
    private int[] rasterizeForestMap(
            @NonNull final DEMDescriptor demDescriptor,
            @NonNull final Cluster<ForestItem> forestCluster,
            @NonNull final MapDesignScheme mapScheme
    ) {
        final D8AlgorithmForForestMap d8AlgorithmForForestMap = new D8AlgorithmForForestMap();
        final int[][] forestMap = d8AlgorithmForForestMap.generateForestMap(demDescriptor, forestCluster, mapScheme);

        final int width = demDescriptor.getDemWidth();
        final int height = demDescriptor.getDemHeight();
        final int[] pixelBuffer = new int[height * width];
        IntStream.range(0, height).parallel().forEach(demY -> {
            for (int demX = 0; demX < width; demX++) {
                pixelBuffer[(demY * width) + demX] = forestMap[demY][demX];
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
        if (mapComposer.getForestProvider().isEmpty()) {
            throw new IllegalStateException("ForestProvider is not present!");
        }
        mapComposer.getForestProvider().get().generateFuture().join(); // wait for the future to complete, so we can access the cluster data
        // @TODO in waiter + getter auslagern auf mapComposer Level

        workPackage.getPipelineArtifacts().getArtifacts().entrySet().stream()
                .filter(entry -> entry.getKey().equals(CreatorForestData.class))
                .findFirst()
                .ifPresentOrElse(
                        (entry) -> {
                            final CreatedArtifact<Cluster<ForestItem>> createdArtifact = entry.getValue();
                            final Cluster<ForestItem> forestCluster = createdArtifact.getData();

                            final int[] rawStructureMap = rasterizeForestMap(workPackage.getMapComposerConfiguration().getDemDescriptor(), forestCluster, workPackage.getMapComposerConfiguration().getMapDesignScheme());
                            workPackage.getPipelineArtifacts().addArtifact(this, rawStructureMap);
                        },
                        () -> log.error("ForestClusterCreator was not found in pipeline artifacts!")
                );
    }

    @NonNull
    public Optional<int[]> findMyArtefactFromWorkPackage(@NonNull final MapComposerWorkPackage workPackage) {
        return workPackage.getPipelineArtifacts().getArtifactFrom(getClass()).map(CreatedArtifact::getData);
    }

}