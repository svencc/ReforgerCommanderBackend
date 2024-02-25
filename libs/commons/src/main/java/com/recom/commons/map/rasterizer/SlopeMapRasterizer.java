package com.recom.commons.map.rasterizer;

import com.recom.commons.calculator.d8algorithm.D8AlgorithmForSlopeAndAspectMap;
import com.recom.commons.calculator.d8algorithm.D8AlgorithmForSlopeMap;
import com.recom.commons.map.PixelBufferMapper;
import com.recom.commons.map.rasterizer.configuration.LayerOrder;
import com.recom.commons.map.rasterizer.configuration.MapLayerRenderer;
import com.recom.commons.map.rasterizer.mapdesignscheme.MapDesignScheme;
import com.recom.commons.model.DEMDescriptor;
import com.recom.commons.model.SlopeAndAspect;
import com.recom.commons.model.maprendererpipeline.MapComposerWorkPackage;
import com.recom.commons.model.maprendererpipeline.MapLayerRendererConfiguration;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.ByteArrayOutputStream;
import java.io.IOException;


@Getter
@Setter
public class SlopeMapRasterizer implements MapLayerRenderer {

    @NonNull
    private MapLayerRendererConfiguration mapLayerRendererConfiguration = MapLayerRendererConfiguration.builder()
            .layerOrder(LayerOrder.HEIGHT_MAP)
            .build();


    @NonNull
    public ByteArrayOutputStream rasterizeSlopeMapPNG(
            @NonNull final DEMDescriptor DEMDescriptor,
            @NonNull final MapDesignScheme mapScheme
    ) throws IOException {
        final int[] pixelBuffer = rasterizeSlopeMapRaw(DEMDescriptor, mapScheme);

        return PixelBufferMapper.map(DEMDescriptor, pixelBuffer);
    }

    public ByteArrayOutputStream magic(
            @NonNull DEMDescriptor DEMDescriptor,
            @NonNull int[] pixelBuffer
    ) throws IOException {
        final BufferedImage image = new BufferedImage(DEMDescriptor.getDemWidth(), DEMDescriptor.getDemHeight(), BufferedImage.TYPE_INT_RGB);
        final int[] imagePixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(pixelBuffer, 0, imagePixels, 0, pixelBuffer.length);

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", outputStream);

        return outputStream;
    }

    @NonNull
    public int[] rasterizeSlopeMapRaw(
            @NonNull final DEMDescriptor DEMDescriptor,
            @NonNull final MapDesignScheme mapScheme
    ) {
        final D8AlgorithmForSlopeAndAspectMap algorithmForSlopeAndAspect = new D8AlgorithmForSlopeAndAspectMap(5.0);
        final D8AlgorithmForSlopeMap d8AlgorithmForSlopeMap = new D8AlgorithmForSlopeMap();

        final SlopeAndAspect[][] slopeAndAspects = algorithmForSlopeAndAspect.generateSlopeAndAspectMap(DEMDescriptor.getDem());
        final int[][] contourMap = d8AlgorithmForSlopeMap.generateSlopeMap(slopeAndAspects, mapScheme);

        final int width = DEMDescriptor.getDemWidth();
        final int height = DEMDescriptor.getDemHeight();

        final int[] pixelBuffer = new int[width * height];
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < height; z++) {
                pixelBuffer[x + z * width] = contourMap[x][z];
            }
        }

        return pixelBuffer;
    }

    @Override
    public void render(@NonNull MapComposerWorkPackage workPackage) {
        final int[] rawSlopeMap = rasterizeSlopeMapRaw(workPackage.getMapComposerConfiguration().getDemDescriptor(), workPackage.getMapComposerConfiguration().getMapDesignScheme());
        workPackage.getPipelineArtifacts().addArtifact(this, rawSlopeMap);
    }

}