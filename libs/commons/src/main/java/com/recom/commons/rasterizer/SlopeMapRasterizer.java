package com.recom.commons.rasterizer;

import com.recom.commons.calculator.d8algorithm.D8AlgorithmForSlopeAndAspectMap;
import com.recom.commons.calculator.d8algorithm.D8AlgorithmForSlopeMap;
import com.recom.commons.model.DEMDescriptor;
import com.recom.commons.model.MapRendererPipelineArtefacts;
import com.recom.commons.model.SlopeAndAspect;
import com.recom.commons.rasterizer.mapdesignscheme.MapDesignScheme;
import com.recom.commons.rasterizer.meta.LayerOrder;
import com.recom.commons.rasterizer.meta.MapLayerPipelineRenderer;
import lombok.Getter;
import lombok.NonNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.ByteArrayOutputStream;
import java.io.IOException;


@Getter
public class SlopeMapRasterizer implements MapLayerPipelineRenderer {

    private final LayerOrder layerOrder = LayerOrder.SLOPE_MAP;
    private final boolean visible = false;

    @NonNull
    public ByteArrayOutputStream rasterizeSlopeMap(
            @NonNull final DEMDescriptor DEMDescriptor,
            @NonNull final MapDesignScheme mapScheme
    ) throws IOException {
        final D8AlgorithmForSlopeAndAspectMap algorithmForSlopeAndAspect = new D8AlgorithmForSlopeAndAspectMap(5.0);
        final D8AlgorithmForSlopeMap d8AlgorithmForSlopeMap = new D8AlgorithmForSlopeMap();

        final SlopeAndAspect[][] slopeAndAspects = algorithmForSlopeAndAspect.generateSlopeAndAspectMap(DEMDescriptor.getDem());
        final int[][] contourMap = d8AlgorithmForSlopeMap.generateSlopeMap(slopeAndAspects, mapScheme);

        final int width = DEMDescriptor.getDem().length;
        final int height = DEMDescriptor.getDem()[0].length;

        final int[] pixelBuffer = new int[width * height];
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < height; z++) {
                pixelBuffer[x + z * width] = contourMap[x][z];
            }
        }

        final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        final int[] imagePixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(pixelBuffer, 0, imagePixels, 0, pixelBuffer.length);

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", outputStream);

        return outputStream;
    }

    @Override
    public @NonNull MapRendererPipelineArtefacts render(@NonNull MapRendererPipelineArtefacts pipelineArtefacts) throws IOException {
        pipelineArtefacts.setRasterizedHeightMap(rasterizeSlopeMap(pipelineArtefacts.getDemDescriptor(), pipelineArtefacts.getMapDesignScheme()));

        return pipelineArtefacts;
    }

}