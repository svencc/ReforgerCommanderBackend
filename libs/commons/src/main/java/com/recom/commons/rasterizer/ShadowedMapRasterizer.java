package com.recom.commons.rasterizer;

import com.recom.commons.calculator.d8algorithm.D8AlgorithmForShadedMap;
import com.recom.commons.calculator.d8algorithm.D8AlgorithmForSlopeAndAspectMap;
import com.recom.commons.model.HeightMapDescriptor;
import com.recom.commons.model.SlopeAndAspect;
import com.recom.commons.rasterizer.mapcolorscheme.ReforgerMapScheme;
import lombok.NonNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class ShadowedMapRasterizer {

    /*
     * DIRTY HACK TO visualize new shaded map
     */
    @NonNull
    public ByteArrayOutputStream rasterizeShadeMap(@NonNull final HeightMapDescriptor heightMapDescriptor) {
        final ReforgerMapScheme mapScheme = new ReforgerMapScheme();
        final float[][] dem = heightMapDescriptor.getHeightMap(); // @TODO rename heightMap to dem
        final D8AlgorithmForSlopeAndAspectMap slopeAndAspectAlgorithm = new D8AlgorithmForSlopeAndAspectMap(5.0);
        final D8AlgorithmForShadedMap shadedMapAlgorithm = new D8AlgorithmForShadedMap();

        final SlopeAndAspect[][] slopeAndAspects = slopeAndAspectAlgorithm.generateSlopeAndAspectMap(dem);
        final int[][] shadedMap = shadedMapAlgorithm.generateShadedMap(slopeAndAspects, mapScheme);

        final int width = heightMapDescriptor.getHeightMap().length;
        final int height = heightMapDescriptor.getHeightMap()[0].length;

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

}