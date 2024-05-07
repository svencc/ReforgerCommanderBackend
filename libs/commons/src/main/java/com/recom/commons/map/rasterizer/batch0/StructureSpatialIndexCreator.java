package com.recom.commons.map.rasterizer.batch0;

import com.recom.commons.calculator.CoordinateConverter;
import com.recom.commons.map.MapComposer;
import com.recom.commons.map.rasterizer.configuration.BatchOrder;
import com.recom.commons.map.rasterizer.configuration.LayerOrder;
import com.recom.commons.map.rasterizer.configuration.MapLayerRasterizer;
import com.recom.commons.model.maprendererpipeline.MapComposerWorkPackage;
import com.recom.commons.model.maprendererpipeline.MapLayerRasterizerConfiguration;
import com.recom.commons.model.maprendererpipeline.dataprovider.SpacialIndex;
import com.recom.commons.model.maprendererpipeline.dataprovider.structure.StructureItem;
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
public class StructureSpatialIndexCreator implements MapLayerRasterizer {

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
            .layerOrder(LayerOrder.STRUCTURE_SPATIAL_INDEX)
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
            final CompletableFuture<List<StructureItem>> future = mapComposer.getStructureProvider().get().generateFuture();
            final List<StructureItem> structureEntities = future.get();

            final int mapWidthInMeter = workPackage.getMapComposerConfiguration().getDemDescriptor().getMapWidthInMeter();
            final int mapHeightInMeter = workPackage.getMapComposerConfiguration().getDemDescriptor().getMapHeightInMeter();
            final int structureCellSizeInMeter = workPackage.getMapComposerConfiguration().getMapDesignScheme().getStructureCellSizeInMeter();

            final SpacialIndex<StructureItem> spatialIndex = createStructureSpacialIndex(mapWidthInMeter, mapHeightInMeter, structureCellSizeInMeter, structureEntities);
            workPackage.getPipelineArtifacts().addArtifact(this, spatialIndex);
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