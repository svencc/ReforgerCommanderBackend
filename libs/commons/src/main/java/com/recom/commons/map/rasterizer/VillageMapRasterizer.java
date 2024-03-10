package com.recom.commons.map.rasterizer;

import com.recom.commons.calculator.CoordinateConverter;
import com.recom.commons.calculator.d8algorithm.D8AlgorithmForVillageMap;
import com.recom.commons.map.MapComposer;
import com.recom.commons.map.rasterizer.configuration.LayerOrder;
import com.recom.commons.map.rasterizer.configuration.MapLayerRasterizer;
import com.recom.commons.map.rasterizer.mapdesignscheme.MapDesignScheme;
import com.recom.commons.model.DEMDescriptor;
import com.recom.commons.model.maprendererpipeline.MapComposerWorkPackage;
import com.recom.commons.model.maprendererpipeline.MapLayerRasterizerConfiguration;
import com.recom.commons.model.maprendererpipeline.dataprovider.SpacialIndex;
import com.recom.commons.model.maprendererpipeline.dataprovider.village.VillageItem;
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
public class VillageMapRasterizer implements MapLayerRasterizer {

    @NonNull
    private final CoordinateConverter coordinateConverter = new CoordinateConverter();
    @NonNull
    private final MapComposer mapComposer;
    @NonNull
    private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
    @NonNull
    private Optional<CompletableFuture<List<VillageItem>>> maybePreperationTask = Optional.empty();


    @NonNull
    private MapLayerRasterizerConfiguration mapLayerRasterizerConfiguration = MapLayerRasterizerConfiguration.builder()
            .rasterizerName(getClass().getSimpleName())
            .layerOrder(LayerOrder.VILLAGE_MAP)
            .visible(false)
            .build();

    @NonNull
    public int[] rasterizeVillageMap(
            @NonNull final DEMDescriptor demDescriptor,
            final int villageCellSize,
            @NonNull final SpacialIndex<VillageItem> spacialIndex,
            @NonNull final MapDesignScheme mapScheme
    ) {
        final D8AlgorithmForVillageMap d8AlgorithmForVillagetMap = new D8AlgorithmForVillageMap(villageCellSize);
        final int[][] villageMap = d8AlgorithmForVillagetMap.generateVillageMap(demDescriptor, spacialIndex, mapScheme);

        final int width = demDescriptor.getDemWidth();
        final int height = demDescriptor.getDemHeight();
        final int[] pixelBuffer = new int[width * height];
        for (int demX = 0; demX < width; demX++) {
            for (int demY = 0; demY < height; demY++) {
                pixelBuffer[demX + demY * width] = villageMap[demX][demY];
            }
        }

        return pixelBuffer;
    }

    @Override
    public String getRasterizerName() {
        return getClass().getSimpleName();
    }

    @Override
    public void prepareAsync(@NonNull final MapComposerWorkPackage workPackage) {
        maybePreperationTask.ifPresent(listCompletableFuture -> listCompletableFuture.cancel(true));
        mapComposer.getVillageProvider().ifPresentOrElse(
                villageProvider -> maybePreperationTask = Optional.of(provideVillageInFuture(workPackage)),
                () -> log.error("VillageMapRasterizer cant prepare data, because no village provider is registered!")
        );
    }

    @NonNull
    private CompletableFuture<List<VillageItem>> provideVillageInFuture(@NonNull final MapComposerWorkPackage workPackage) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                assert mapComposer.getVillageProvider().isPresent();

                return mapComposer.getVillageProvider().get().provide();
            } catch (final Throwable t) {
                log.error("Failed to prepare village data!", t);
                workPackage.getReport().logException(t);

                return List.of();
            }
        }, executorService);
    }

    @Override
    public void render(@NonNull MapComposerWorkPackage workPackage) {
        if (maybePreperationTask.isPresent()) {
            final List<VillageItem> villageEntities = maybePreperationTask.get().join();

            final int mapWidthInMeter = workPackage.getMapComposerConfiguration().getDemDescriptor().getMapWidthInMeter();
            final int mapHeightInMeter = workPackage.getMapComposerConfiguration().getDemDescriptor().getMapHeightInMeter();
            final int villageCellSizeInMeter = 10; // 10 meter? @TODO extract to conf

            final SpacialIndex<VillageItem> spatialIndex = createVillageSpacialIndex(mapWidthInMeter, mapHeightInMeter, villageCellSizeInMeter, villageEntities);

            final int[] rawVillageMap = rasterizeVillageMap(workPackage.getMapComposerConfiguration().getDemDescriptor(), villageCellSizeInMeter, spatialIndex, workPackage.getMapComposerConfiguration().getMapDesignScheme());
            workPackage.getPipelineArtifacts().addArtifact(this, rawVillageMap);
        } else {
            log.error("VillageMapRasterizer is not prepared, and prepareAsync was not called in advance!");
            return;
        }
    }

    @NonNull
    private SpacialIndex<VillageItem> createVillageSpacialIndex(
            final int mapWidthInMeter,
            final int mapHeightInMeter,
            final int villageCellSizeInMeter,
            @NonNull final List<VillageItem> villageEntities
    ) {
        final SpacialIndex<VillageItem> spatialIndex = new SpacialIndex<>(mapWidthInMeter, mapHeightInMeter, villageCellSizeInMeter);
        villageEntities.forEach(villageItem -> {
            final double x = villageItem.getCoordinateX().doubleValue();
            final double y = coordinateConverter.threeDeeZToTwoDeeY(villageItem.getCoordinateY().doubleValue(), mapHeightInMeter);
            spatialIndex.put(x, y, villageItem);
        });

        return spatialIndex;
    }

}