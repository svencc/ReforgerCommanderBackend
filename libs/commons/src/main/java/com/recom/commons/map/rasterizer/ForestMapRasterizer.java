package com.recom.commons.map.rasterizer;

import com.recom.commons.map.MapComposer;
import com.recom.commons.map.rasterizer.configuration.LayerOrder;
import com.recom.commons.map.rasterizer.configuration.MapLayerRasterizer;
import com.recom.commons.map.rasterizer.mapdesignscheme.MapDesignScheme;
import com.recom.commons.math.Round;
import com.recom.commons.model.maprendererpipeline.MapComposerWorkPackage;
import com.recom.commons.model.maprendererpipeline.MapLayerRasterizerConfiguration;
import com.recom.commons.model.maprendererpipeline.dataprovider.forest.ForestItem;
import com.recom.commons.model.maprendererpipeline.dataprovider.forest.SpacialIndex;
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

@Slf4j
@Getter
@Setter
@RequiredArgsConstructor
public class ForestMapRasterizer implements MapLayerRasterizer {

    @NonNull
    private final MapComposer mapComposer;
    @NonNull
    private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
    @NonNull
    private Optional<CompletableFuture<List<ForestItem>>> maybePreperationTask = Optional.empty();


    @NonNull
    private MapLayerRasterizerConfiguration mapLayerRasterizerConfiguration = MapLayerRasterizerConfiguration.builder()
            .rasterizerName(getClass().getSimpleName())
            .layerOrder(LayerOrder.FOREST_MAP)
            .visible(false)
            .build();

    @NonNull
    public int[] rasterizeForestMap(
            final SpacialIndex<ForestItem> spacialIndex,
            @NonNull final MapDesignScheme mapScheme
    ) {
//        final D8AlgorithmForSlopeAndAspectMap algorithmForSlopeAndAspect = new D8AlgorithmForSlopeAndAspectMap(5.0);
//        final D8AlgorithmForSlopeMap d8AlgorithmForSlopeMap = new D8AlgorithmForSlopeMap();
//
//        final SlopeAndAspect[][] slopeAndAspects = algorithmForSlopeAndAspect.generateSlopeAndAspectMap(DEMDescriptor.getDem());
//        final int[][] contourMap = d8AlgorithmForSlopeMap.generateSlopeMap(slopeAndAspects, mapScheme);
//
// @TODO <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

        final int width = spacialIndex.getWidth();
        final int height = spacialIndex.getHeight();

        final int[] pixelBuffer = new int[width * height];
//        for (int x = 0; x < width; x++) {
//            for (int z = 0; z < height; z++) {
//                pixelBuffer[x + z * width] = contourMap[x][z];
//            }
//        }

        return pixelBuffer;
    }

    @Override
    public String getRasterizerName() {
        return getClass().getSimpleName();
    }

    @Override
    public void prepareAsync(@NonNull MapComposerWorkPackage workPackage) {
        maybePreperationTask.ifPresent(listCompletableFuture -> listCompletableFuture.cancel(true));
        mapComposer.getForestProvider().ifPresentOrElse(
                forestProvider -> maybePreperationTask = Optional.of(provideForestInFuture(workPackage)),
                () -> log.error("ForestMapRasterizer cant prepare data, because no forest provider is registered!")
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
    public void render(@NonNull MapComposerWorkPackage workPackage) {
        if (maybePreperationTask.isPresent()) {
            final List<ForestItem> forestEntities = maybePreperationTask.get().join();

            final int demWidth = workPackage.getMapComposerConfiguration().getDemDescriptor().getDemWidth();
            final int demHeight = workPackage.getMapComposerConfiguration().getDemDescriptor().getDemHeight();

            final SpacialIndex<ForestItem> spatialIndex = new SpacialIndex<>(demWidth, demHeight);
            forestEntities.forEach(forestItem -> {
                final int x = Round.halfUp(forestItem.getCoordinateX().doubleValue());
                final int y = Round.halfUp(forestItem.getCoordinateY().doubleValue());
                if (x >= 0 && x < demWidth && y >= 0 && y < demHeight) {
                    spatialIndex.put(x, y, forestItem);
                }
            });

            final int[] rawForestMap = rasterizeForestMap(spatialIndex, workPackage.getMapComposerConfiguration().getMapDesignScheme());
            workPackage.getPipelineArtifacts().addArtifact(this, rawForestMap);
        } else {
            log.error("ForestMapRasterizer is not prepared, and prepareAsync was not called in advance!");
            return;
        }
    }

}