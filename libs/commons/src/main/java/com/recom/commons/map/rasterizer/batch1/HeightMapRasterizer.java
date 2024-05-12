package com.recom.commons.map.rasterizer.batch1;

import com.recom.commons.map.rasterizer.configuration.BatchOrder;
import com.recom.commons.map.rasterizer.configuration.LayerOrder;
import com.recom.commons.map.rasterizer.configuration.MapLayerRasterizer;
import com.recom.commons.map.rasterizer.interpolation.DEMUpscaleAlgorithmBilinear;
import com.recom.commons.model.DEMDescriptor;
import com.recom.commons.model.maprendererpipeline.CreatedArtifact;
import com.recom.commons.model.maprendererpipeline.MapComposerWorkPackage;
import com.recom.commons.model.maprendererpipeline.MapLayerRasterizerConfiguration;
import lombok.Getter;
import lombok.NonNull;

import java.awt.*;
import java.util.Optional;
import java.util.stream.IntStream;


public class HeightMapRasterizer implements MapLayerRasterizer<int[]> {

    @NonNull
    private final DEMUpscaleAlgorithmBilinear interpolator;

    @Getter
    @NonNull
    private final MapLayerRasterizerConfiguration mapLayerRasterizerConfiguration = MapLayerRasterizerConfiguration.builder()
            .rasterizerName(getClass().getSimpleName())
            .batch(BatchOrder.BATCH_1)
            .layerOrder(LayerOrder.HEIGHT_MAP)
            .visible(false)
            .enabled(false)
            .build();

    public HeightMapRasterizer() {
        interpolator = new DEMUpscaleAlgorithmBilinear();
    }

    @NonNull
    public int[] rasterizeHeightMap(
            @NonNull final DEMDescriptor DEMDescriptor,
            final int scale
    ) {
        float[][] interpolatedDem = interpolator.scaleUp(DEMDescriptor, scale);
        final DEMDescriptor demDescriptorClone = DEMDescriptor.clone();
        demDescriptorClone.setDem(interpolatedDem);

        return rasterizeHeightMap(demDescriptorClone);
    }

    @NonNull
    public int[] rasterizeHeightMap(@NonNull final DEMDescriptor demDescriptor) {
        final int width = demDescriptor.getDemWidth();
        final int height = demDescriptor.getDemHeight();
        final int[] imageBuffer = new int[height * width];

        final float heightRange = demDescriptor.getMaxHeight() - demDescriptor.getSeaLevel();
        final float depthRange = demDescriptor.getMaxWaterDepth() - demDescriptor.getSeaLevel();

        IntStream.range(0, height).parallel().forEach(coordinateY -> {
            for (int coordinateX = 0; coordinateX < width; coordinateX++) {
                final float heightValue = demDescriptor.getDem()[coordinateY][coordinateX];

                final Color color;
                if (heightValue >= demDescriptor.getSeaLevel()) {
                    // map height to color
                    final float dynamicHeightUnit = (heightValue - demDescriptor.getSeaLevel()) / heightRange;
                    int grayValue = (int) (255 * dynamicHeightUnit); // normalize to 0..255
                    grayValue = Math.min(Math.max(grayValue, 0), 255); // ensure that the value is in the valid range
                    color = new Color(grayValue, grayValue, grayValue);
                } else {
                    // map depth to water color
                    final float dynamicDepthUnit = (heightValue - demDescriptor.getSeaLevel()) / depthRange;
                    int blueValue = (int) (255 * (dynamicDepthUnit)); //  // normalize to 0..255
                    blueValue = Math.min(Math.max(blueValue, 0), 255); // ensure that the value is in the valid range
                    color = new Color((int) (blueValue * 0.77), (int) (192 * 0.94), blueValue);
                }

                imageBuffer[(coordinateY * width) + coordinateX] = color.getRGB();
            }
        });

        return imageBuffer;
    }

    @Override
    public String getRasterizerName() {
        return getClass().getSimpleName();
    }

    @Override
    public void render(@NonNull final MapComposerWorkPackage workPackage) {
        final int[] rawHeightMap = rasterizeHeightMap(workPackage.getMapComposerConfiguration().getDemDescriptor());
        workPackage.getPipelineArtifacts().addArtifact(this, rawHeightMap);
    }

    @NonNull
    public Optional<int[]> findMyArtefactFromWorkPackage(@NonNull final MapComposerWorkPackage workPackage) {
        return workPackage.getPipelineArtifacts().getArtifactFrom(getClass()).map(CreatedArtifact::getData);
    }

}