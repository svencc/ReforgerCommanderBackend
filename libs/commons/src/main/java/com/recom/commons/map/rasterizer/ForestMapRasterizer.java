package com.recom.commons.map.rasterizer;

import com.recom.commons.calculator.CoordinateConverter;
import com.recom.commons.calculator.d8algorithm.D8AlgorithmForForestMap;
import com.recom.commons.map.MapComposer;
import com.recom.commons.map.rasterizer.configuration.LayerOrder;
import com.recom.commons.map.rasterizer.configuration.MapLayerRasterizer;
import com.recom.commons.map.rasterizer.mapdesignscheme.MapDesignScheme;
import com.recom.commons.model.DEMDescriptor;
import com.recom.commons.model.maprendererpipeline.MapComposerWorkPackage;
import com.recom.commons.model.maprendererpipeline.MapLayerRasterizerConfiguration;
import com.recom.commons.model.maprendererpipeline.dataprovider.SpacialIndex;
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
public class ForestMapRasterizer implements MapLayerRasterizer {

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
            .layerOrder(LayerOrder.FOREST_MAP)
            .visible(false)
            .build();

    @NonNull
    private int[] rasterizeForestMap(
            @NonNull final DEMDescriptor demDescriptor,
            final int forestCellSize,
            @NonNull final SpacialIndex<ForestItem> spacialIndex,
            @NonNull final MapDesignScheme mapScheme
    ) {
        final D8AlgorithmForForestMap d8AlgorithmForForestMap = new D8AlgorithmForForestMap(forestCellSize);
        final int[][] forestMap = d8AlgorithmForForestMap.generateForestMap(demDescriptor, spacialIndex, mapScheme);

        final int width = demDescriptor.getDemWidth();
        final int height = demDescriptor.getDemHeight();
        final int[] pixelBuffer = new int[width * height];
        IntStream.range(0, width).parallel().forEach(demX -> {
            for (int demY = 0; demY < height; demY++) {
                pixelBuffer[demX + demY * width] = forestMap[demX][demY];
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
        maybePreparationTask.ifPresent(listCompletableFuture -> listCompletableFuture.cancel(true));
        mapComposer.getForestProvider().ifPresentOrElse(
                forestProvider -> maybePreparationTask = Optional.of(provideForestInFuture(workPackage)),
                () -> log.error("{} cant prepare data, because no forest provider is registered!", getClass().getSimpleName())
        );
    }

    @NonNull
    private CompletableFuture<List<ForestItem>> provideForestInFuture(@NonNull final MapComposerWorkPackage workPackage) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                assert mapComposer.getForestProvider().isPresent();

                return mapComposer.getForestProvider().get().provide();
            } catch (final Throwable t) {
                log.error("Failed to prepare forest data!", t);
                workPackage.getReport().logException(t);

                return List.of();
            }
        }, executorService);
    }

    @Override
    public void render(@NonNull final MapComposerWorkPackage workPackage) {
        if (maybePreparationTask.isPresent()) {
            final List<ForestItem> forestEntities = maybePreparationTask.get().join();

            final int mapWidthInMeter = workPackage.getMapComposerConfiguration().getDemDescriptor().getMapWidthInMeter();
            final int mapHeightInMeter = workPackage.getMapComposerConfiguration().getDemDescriptor().getMapHeightInMeter();
            final int forestCellSizeInMeter = workPackage.getMapComposerConfiguration().getMapDesignScheme().getForestCellSizeInMeter();

            final SpacialIndex<ForestItem> spatialIndex = createForestSpacialIndex(mapWidthInMeter, mapHeightInMeter, forestCellSizeInMeter, forestEntities);

            final int[] rawForestMap = rasterizeForestMap(workPackage.getMapComposerConfiguration().getDemDescriptor(), forestCellSizeInMeter, spatialIndex, workPackage.getMapComposerConfiguration().getMapDesignScheme());
            workPackage.getPipelineArtifacts().addArtifact(this, rawForestMap);
        } else {
            log.error("ForestMapRasterizer is not prepared, and prepareAsync was not called in advance!");
            return;
        }
    }

    @NonNull
    private SpacialIndex<ForestItem> createForestSpacialIndex(
            final int mapWidthInMeter,
            final int mapHeightInMeter,
            final int forestCellSizeInMeter,
            @NonNull final List<ForestItem> forestEntities
    ) {
        final SpacialIndex<ForestItem> spatialIndex = new SpacialIndex<>(mapWidthInMeter, mapHeightInMeter, forestCellSizeInMeter);
        forestEntities.forEach(forestItem -> {
            final double x = forestItem.getCoordinateX().doubleValue();
            final double y = coordinateConverter.threeDeeZToTwoDeeY(forestItem.getCoordinateY().doubleValue(), mapHeightInMeter);
            spatialIndex.put(x, y, forestItem);
        });

        return spatialIndex;
    }

}