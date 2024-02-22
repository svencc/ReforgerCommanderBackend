package com.recom.commons.rasterizer;

import com.recom.commons.calculator.D8CalculatorForSlopeAndAspectMaps;
import com.recom.commons.model.SlopeAndAspect;
import com.recom.commons.rasterizer.mapcolorscheme.ReforgerMapScheme;
import lombok.NonNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class HeightmapRasterizer {

    @NonNull
    private final HeightmapScaler heightmapScaler;

    public HeightmapRasterizer() {
        heightmapScaler = new HeightmapScaler();
    }

    @NonNull
    public ByteArrayOutputStream rasterizeHeightMapPNG(@NonNull final HeightMapDescriptor heightMapDescriptor) throws IOException {
        final int[] pixelBuffer = rasterizeHeightMapRGB(heightMapDescriptor);

        final int width = heightMapDescriptor.getHeightMap().length;
        final int height = heightMapDescriptor.getHeightMap()[0].length;
        final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        final int[] imagePixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(pixelBuffer, 0, imagePixels, 0, pixelBuffer.length);

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", outputStream);

        return outputStream;
    }

    public int[] rasterizeHeightMapRGB(@NonNull final HeightMapDescriptor command) {
        final int width = command.getHeightMap().length;
        final int height = command.getHeightMap()[0].length;
        final int[] imageBuffer = new int[width * height];

        final float heightRange = command.getMaxHeight() - command.getSeaLevel();
        final float depthRange = command.getMaxWaterDepth() - command.getSeaLevel();

        for (int x = 0; x < width; x++) {
            for (int z = 0; z < height; z++) {
                final float heightValue = command.getHeightMap()[x][z];
                Color color;

                if (heightValue >= command.getSeaLevel()) {
                    // map height to color
                    final float dynamicHeightUnit = (heightValue - command.getSeaLevel()) / heightRange;
                    int grayValue = (int) (255 * dynamicHeightUnit); // normalize to 0..255
                    grayValue = Math.min(Math.max(grayValue, 0), 255); // ensure that the value is in the valid range
                    color = new Color(grayValue, grayValue, grayValue);
                } else {
                    // map depth to water color
                    final float dynamicDepthUnit = (heightValue - command.getSeaLevel()) / depthRange;
                    int blueValue = (int) (255 * (dynamicDepthUnit)); //  // normalize to 0..255
                    blueValue = Math.min(Math.max(blueValue, 0), 255); // ensure that the value is in the valid range
                    color = new Color((int) (blueValue * 0.77), (int) (192 * 0.94), blueValue);
                }

                imageBuffer[x + z * width] = color.getRGB();
            }
        }

        return imageBuffer;
    }

    public int[] rasterizeHeightMapRGB(
            @NonNull final HeightMapDescriptor heightMapDescriptor,
            final int scale
    ) {
        final int[] originalHeightMap = rasterizeHeightMapRGB(heightMapDescriptor);

        return heightmapScaler.scaleMap(heightMapDescriptor, scale, originalHeightMap);
    }


    /*
     * DIRTY HACK TO visualize new shaded map
     */
    @NonNull
    public ByteArrayOutputStream rasterizeShadeMap(@NonNull final HeightMapDescriptor heightMapDescriptor) {
        final ReforgerMapScheme mapScheme = new ReforgerMapScheme();
        final float[][] dem = heightMapDescriptor.getHeightMap(); // @TODO rename heightMap to dem
        final D8CalculatorForSlopeAndAspectMaps algorithm = new D8CalculatorForSlopeAndAspectMaps(5.0);

        final SlopeAndAspect[][] slopeAndAspects = algorithm.calculateSlopeAndAspectMap(dem);
        final int[][] shadedMap = algorithm.calculateShadedMap(slopeAndAspects, mapScheme);


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

    /*
     * DIRTY HACK TO visualize new contour map
     */
    @NonNull
    public ByteArrayOutputStream rasterizeContourMap(@NonNull final HeightMapDescriptor heightMapDescriptor) {
        final ReforgerMapScheme mapScheme = new ReforgerMapScheme();
        final D8CalculatorForSlopeAndAspectMaps algorithm = new D8CalculatorForSlopeAndAspectMaps(5.0);

        final int[][] contourMap = algorithm.calculateContourMap(heightMapDescriptor, mapScheme);


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

    /*
     * DIRTY HACK TO visualize new slope map
     */
    @NonNull
    public ByteArrayOutputStream rasterizeSlopeMap(@NonNull final HeightMapDescriptor heightMapDescriptor) {
        final ReforgerMapScheme mapScheme = new ReforgerMapScheme();
        final D8CalculatorForSlopeAndAspectMaps algorithm = new D8CalculatorForSlopeAndAspectMaps(5.0);

        final SlopeAndAspect[][] slopeAndAspects = algorithm.calculateSlopeAndAspectMap(heightMapDescriptor.getHeightMap());
        final int[][] contourMap = algorithm.extractSlopeMap(slopeAndAspects, mapScheme);

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