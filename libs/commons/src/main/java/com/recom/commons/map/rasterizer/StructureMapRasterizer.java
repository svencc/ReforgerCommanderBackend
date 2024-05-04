package com.recom.commons.map.rasterizer;

import com.recom.commons.calculator.CoordinateConverter;
import com.recom.commons.calculator.d8algorithm.D8AlgorithmForStructureClusterMap;
import com.recom.commons.calculator.d8algorithm.D8AlgorithmForStructureMap;
import com.recom.commons.map.MapComposer;
import com.recom.commons.map.rasterizer.configuration.LayerOrder;
import com.recom.commons.map.rasterizer.configuration.MapLayerRasterizer;
import com.recom.commons.map.rasterizer.mapdesignscheme.MapDesignScheme;
import com.recom.commons.model.DEMDescriptor;
import com.recom.commons.model.maprendererpipeline.MapComposerWorkPackage;
import com.recom.commons.model.maprendererpipeline.MapLayerRasterizerConfiguration;
import com.recom.commons.model.maprendererpipeline.dataprovider.SpacialIndex;
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
public class StructureMapRasterizer implements MapLayerRasterizer {

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
            .layerOrder(LayerOrder.STRUCTURE_MAP)
            .visible(false)
            .build();

    @NonNull
    private int[] rasterizeStructureMap(
            @NonNull final DEMDescriptor demDescriptor,
            final int structureCellSize,
            @NonNull final SpacialIndex<StructureItem> spacialIndex,
            @NonNull final MapDesignScheme mapScheme
    ) {
        final D8AlgorithmForStructureMap d8AlgorithmForStructuretMap = new D8AlgorithmForStructureMap(structureCellSize);
        final int[][] structureMap = d8AlgorithmForStructuretMap.generateStructureMap(demDescriptor, spacialIndex, mapScheme);

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

    @NonNull
    private int[] rasterizeStructureClusterMap(
            @NonNull final DEMDescriptor demDescriptor,
            final int structureCellSize,
            @NonNull final SpacialIndex<StructureItem> spacialIndex,
            @NonNull final MapDesignScheme mapScheme
    ) {
        final D8AlgorithmForStructureClusterMap d8AlgorithmForStructureClusterMap = new D8AlgorithmForStructureClusterMap(structureCellSize);
        final int[][] structureMap = d8AlgorithmForStructureClusterMap.generateStructureClusterMap(demDescriptor, spacialIndex, mapScheme);

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
    public void prepareAsync(@NonNull final MapComposerWorkPackage workPackage) {
        maybePreparationTask.ifPresent(listCompletableFuture -> listCompletableFuture.cancel(true));
        mapComposer.getStructureProvider().ifPresentOrElse(
                structureProvider -> maybePreparationTask = Optional.of(provideStructureInFuture(workPackage)),
                () -> log.error("{} cant prepare data, because no structure provider is registered!", getClass().getSimpleName())
        );
    }

    @NonNull
    private CompletableFuture<List<StructureItem>> provideStructureInFuture(@NonNull final MapComposerWorkPackage workPackage) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                assert mapComposer.getStructureProvider().isPresent();

                return mapComposer.getStructureProvider().get().provide();
            } catch (final Throwable t) {
                log.error("Failed to prepare structure data!", t);
                workPackage.getReport().logException(t);

                return List.of();
            }
        }, executorService);
    }

    @Override
    public void render(@NonNull final MapComposerWorkPackage workPackage) {
        if (maybePreparationTask.isPresent()) {
            final List<StructureItem> structureEntities = maybePreparationTask.get().join();

            final int mapWidthInMeter = workPackage.getMapComposerConfiguration().getDemDescriptor().getMapWidthInMeter();
            final int mapHeightInMeter = workPackage.getMapComposerConfiguration().getDemDescriptor().getMapHeightInMeter();
            final int structureCellSizeInMeter = workPackage.getMapComposerConfiguration().getMapDesignScheme().getStructureCellSizeInMeter();

            final SpacialIndex<StructureItem> spatialIndex = createStructureSpacialIndex(mapWidthInMeter, mapHeightInMeter, structureCellSizeInMeter, structureEntities);

            final int[] rawStructureMap = rasterizeStructureMap(workPackage.getMapComposerConfiguration().getDemDescriptor(), structureCellSizeInMeter, spatialIndex, workPackage.getMapComposerConfiguration().getMapDesignScheme());
            // @TODO we have to do it this way!
            final int[] rawStructureClusterMap = rasterizeStructureClusterMap(workPackage.getMapComposerConfiguration().getDemDescriptor(), structureCellSizeInMeter, spatialIndex, workPackage.getMapComposerConfiguration().getMapDesignScheme());

            workPackage.getPipelineArtifacts().addArtifact(this, rawStructureMap);
        } else {
            log.error("StructureMapRasterizer is not prepared, and prepareAsync was not called in advance!");
            return;
        }
    }

    @NonNull
    private SpacialIndex<StructureItem> createStructureSpacialIndex(
            final int mapWidthInMeter,
            final int mapHeightInMeter,
            final int structureCellSizeInMeter,
            @NonNull final List<StructureItem> structureEntities
    ) {
        final SpacialIndex<StructureItem> spatialIndex = new SpacialIndex<>(mapWidthInMeter, mapHeightInMeter, structureCellSizeInMeter);
        structureEntities.forEach(structureItem -> {
            final double x = structureItem.getCoordinateX().doubleValue();
            final double y = coordinateConverter.threeDeeZToTwoDeeY(structureItem.getCoordinateY().doubleValue(), mapHeightInMeter);
            spatialIndex.put(x, y, structureItem);
        });

        return spatialIndex;
    }

}