package com.recom.commons.map.rasterizer.batch0;

import com.recom.commons.calculator.CoordinateConverter;
import com.recom.commons.map.MapComposer;
import com.recom.commons.map.rasterizer.configuration.BatchOrder;
import com.recom.commons.map.rasterizer.configuration.LayerOrder;
import com.recom.commons.map.rasterizer.configuration.MapLayerRasterizer;
import com.recom.commons.model.maprendererpipeline.MapComposerWorkPackage;
import com.recom.commons.model.maprendererpipeline.MapLayerRasterizerConfiguration;
import com.recom.commons.model.maprendererpipeline.dataprovider.SpacialIndex;
import com.recom.commons.model.maprendererpipeline.dataprovider.forest.ForestItem;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Getter
@Setter
@RequiredArgsConstructor
public class ForestSpatialIndexCreator implements MapLayerRasterizer {

    @NonNull
    private final CoordinateConverter coordinateConverter = new CoordinateConverter();
    @NonNull
    private final MapComposer mapComposer;
    @NonNull
    private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();


    @NonNull
    private MapLayerRasterizerConfiguration mapLayerRasterizerConfiguration = MapLayerRasterizerConfiguration.builder()
            .rasterizerName(getClass().getSimpleName())
            .batch(BatchOrder.PREPARE_BATCH)
            .layerOrder(LayerOrder.FOREST_SPATIAL_INDEX)
            .visible(false)
            .build();

    @Override
    public String getRasterizerName() {
        return getClass().getSimpleName();
    }

    @Override
    @SneakyThrows
    public void render(@NonNull final MapComposerWorkPackage workPackage) {
        if (mapComposer.getStructureProvider().isPresent()) {
            final CompletableFuture<List<ForestItem>> future = mapComposer.getForestProvider().get().generateFuture();
            final List<ForestItem> structureEntities = future.get();

            final int mapWidthInMeter = workPackage.getMapComposerConfiguration().getDemDescriptor().getMapWidthInMeter();
            final int mapHeightInMeter = workPackage.getMapComposerConfiguration().getDemDescriptor().getMapHeightInMeter();
            final int structureCellSizeInMeter = workPackage.getMapComposerConfiguration().getMapDesignScheme().getStructureCellSizeInMeter();

            final SpacialIndex<ForestItem> spatialIndex = createForestSpacialIndex(mapWidthInMeter, mapHeightInMeter, structureCellSizeInMeter, structureEntities);
            workPackage.getPipelineArtifacts().addArtifact(this, spatialIndex);
        }
    }

    @NonNull
    private SpacialIndex<ForestItem> createForestSpacialIndex( //@ TODO sollte vielleicht ClusterIndex erstellen!?
            final int mapWidthInMeter,
            final int mapHeightInMeter,
            final int forestCellSizeInMeter,
            @NonNull final List<ForestItem> forestEntities
    ) {
        final SpacialIndex<ForestItem> spatialIndex = new SpacialIndex<>(mapWidthInMeter, mapHeightInMeter, forestCellSizeInMeter);
        forestEntities.forEach(forestItem -> {
            final int x = forestItem.getCoordinateX().intValue();
            final int y = coordinateConverter.threeDeeZToTwoDeeY(forestItem.getCoordinateY().intValue(), mapHeightInMeter);
            spatialIndex.put(x, y, forestItem);
        });

        return spatialIndex;
    }

}