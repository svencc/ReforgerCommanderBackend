package com.recom.commons.rasterizer;

import com.recom.commons.calculator.d8algorithm.D8AlgorithmForShadedMap;
import com.recom.commons.calculator.d8algorithm.D8AlgorithmForSlopeAndAspectMap;
import com.recom.commons.model.DEMDescriptor;
import com.recom.commons.model.MapRendererPipelineArtefacts;
import com.recom.commons.model.SlopeAndAspect;
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
public class ShadowedMapRasterizer implements MapLayerPipelineRenderer {

    private final LayerOrder layerOrder = LayerOrder.SHADOWED_MAP;
    private final boolean visible = true;


    @NonNull
    public ByteArrayOutputStream rasterizeShadowedMap(
            @NonNull final DEMDescriptor DEMDescriptor,
            @NonNull final MapDesignScheme mapScheme
    ) {
        final float[][] dem = DEMDescriptor.getDem(); // @TODO rename heightMap to dem
        final D8AlgorithmForSlopeAndAspectMap slopeAndAspectAlgorithm = new D8AlgorithmForSlopeAndAspectMap(5.0);
        final D8AlgorithmForShadedMap shadedMapAlgorithm = new D8AlgorithmForShadedMap();

        final SlopeAndAspect[][] slopeAndAspects = slopeAndAspectAlgorithm.generateSlopeAndAspectMap(dem);
        final int[][] shadedMap = shadedMapAlgorithm.generateShadedMap(slopeAndAspects, mapScheme);

        final int width = DEMDescriptor.getDem().length;
        final int height = DEMDescriptor.getDem()[0].length;

        final int[] pixelBuffer = new int[width * height];
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < height; z++) {
                pixelBuffer[x + z * width] = shadedMap[x][z];
            }
        }

        final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        final int[] imagePixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(pixelBuffer, 0, imagePixels, 0, pixelBuffer.length);

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", outputStream);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        return outputStream;
    }

    @Override
    public @NonNull MapRendererPipelineArtefacts render(@NonNull final MapRendererPipelineArtefacts pipelineArtefacts) throws IOException {
        pipelineArtefacts.setRasterizedHeightMap(rasterizeShadowedMap(pipelineArtefacts.getDemDescriptor(), pipelineArtefacts.getMapDesignScheme()));

        return pipelineArtefacts;
    }

}