package com.recom.commons.map.rasterizer.batch1;

import com.recom.commons.calculator.d8algorithm.D8AlgorithmForContourMap;
import com.recom.commons.map.rasterizer.configuration.BatchOrder;
import com.recom.commons.map.rasterizer.configuration.LayerOrder;
import com.recom.commons.map.rasterizer.configuration.MapLayerRasterizer;
import com.recom.commons.map.rasterizer.mapdesignscheme.MapDesignScheme;
import com.recom.commons.model.DEMDescriptor;
import com.recom.commons.model.maprendererpipeline.CreatedArtifact;
import com.recom.commons.model.maprendererpipeline.MapComposerWorkPackage;
import com.recom.commons.model.maprendererpipeline.MapLayerRasterizerConfiguration;
import lombok.Getter;
import lombok.NonNull;

import java.util.Optional;
import java.util.stream.IntStream;


@Getter
public class ContourLineMapRasterizer implements MapLayerRasterizer<int[]> {

    @NonNull
    private final MapLayerRasterizerConfiguration mapLayerRasterizerConfiguration = MapLayerRasterizerConfiguration.builder()
            .rasterizerName(getClass().getSimpleName())
            .batch(BatchOrder.BATCH_1)
            .layerOrder(LayerOrder.CONTOUR_MAP)
            .build();

    @NonNull
    private int[] rasterizeContourMap(
            @NonNull final DEMDescriptor DEMDescriptor,
            @NonNull final MapDesignScheme mapScheme
    ) {
        final D8AlgorithmForContourMap algorithmForContourMap = new D8AlgorithmForContourMap();

        final int[][] contourMap = algorithmForContourMap.generateContourMap(DEMDescriptor, mapScheme);

        final int width = DEMDescriptor.getDemWidth();
        final int height = DEMDescriptor.getDemHeight();

        final int[] pixelBuffer = new int[height * width];
        IntStream.range(0, height).parallel().forEach(demY -> {
            for (int demX = 0; demX < width; demX++) {
                pixelBuffer[(demY * width) + demX] = contourMap[demY][demX];
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
        final int[] rawContourMap = rasterizeContourMap(workPackage.getMapComposerConfiguration().getDemDescriptor(), workPackage.getMapComposerConfiguration().getMapDesignScheme());
        workPackage.getPipelineArtifacts().addArtifact(this, rawContourMap);
    }

    @NonNull
    public Optional<int[]> findMyArtefactFromWorkPackage(@NonNull final MapComposerWorkPackage workPackage) {
        return workPackage.getPipelineArtifacts().getArtifactFrom(getClass()).map(CreatedArtifact::getData);
    }

}