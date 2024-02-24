package com.recom.commons.rasterizer;

import com.recom.commons.calculator.d8algorithm.D8AlgorithmForContourMap;
import com.recom.commons.model.DEMDescriptor;
import com.recom.commons.model.MapRendererPipelineArtefacts;
import com.recom.commons.rasterizer.mapcolorscheme.MapDesignScheme;
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
public class ContourMapRasterizer implements MapLayerPipelineRenderer {

    private final LayerOrder layerOrder = LayerOrder.HEIGHT_MAP;
    private final boolean visible = true;

    @NonNull
    public ByteArrayOutputStream rasterizeContourMap(
            @NonNull final DEMDescriptor DEMDescriptor,
            @NonNull final MapDesignScheme mapScheme
    ) throws IOException {
        final D8AlgorithmForContourMap algorithmForContourMap = new D8AlgorithmForContourMap();

        final int[][] contourMap = algorithmForContourMap.generateContourMap(DEMDescriptor, mapScheme);

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

    @NonNull
    @Override
    public MapRendererPipelineArtefacts render(@NonNull final MapRendererPipelineArtefacts pipelineArtefacts) throws IOException {
        pipelineArtefacts.setRasterizedContourMap(rasterizeContourMap(pipelineArtefacts.getDemDescriptor(), pipelineArtefacts.getMapDesignScheme()));

        return pipelineArtefacts;
    }

}