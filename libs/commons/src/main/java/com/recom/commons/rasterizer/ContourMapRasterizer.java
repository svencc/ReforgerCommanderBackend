package com.recom.commons.rasterizer;

import com.recom.commons.calculator.d8algorithm.D8AlgorithmForContourMap;
import com.recom.commons.model.HeightMapDescriptor;
import com.recom.commons.rasterizer.mapcolorscheme.ReforgerMapScheme;
import lombok.NonNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class ContourMapRasterizer {

    @NonNull
    public ByteArrayOutputStream rasterizeContourMap(@NonNull final HeightMapDescriptor heightMapDescriptor) {
        final ReforgerMapScheme mapScheme = new ReforgerMapScheme();
        final D8AlgorithmForContourMap algorithmForContourMap = new D8AlgorithmForContourMap();

        final int[][] contourMap = algorithmForContourMap.generateContourMap(heightMapDescriptor, mapScheme);

        final int width = heightMapDescriptor.getHeightMap().length;
        final int height = heightMapDescriptor.getHeightMap()[0].length;

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
        try {
            ImageIO.write(image, "png", outputStream);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        return outputStream;
    }

}