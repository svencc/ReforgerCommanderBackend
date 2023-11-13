package com.recom.service.map;

import com.recom.entity.MapTopographyEntity;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HeightmapGeneratorService {


    @NonNull
    public ByteArrayOutputStream createHeightMap(
            final float seaLevel,
            final float maxHeight,
            final float maxWaterDepth,
            final float[][] surfaceData
    ) throws IOException {
        int width = surfaceData.length;
        int height = surfaceData[0].length;
        final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        final float heightRange = maxHeight - seaLevel;
        final float depthRange = maxWaterDepth - seaLevel;

        for (int x = 0; x < width; x++) {
            for (int z = 0; z < height; z++) {
                final float heightValue = surfaceData[x][z];
                Color color;

                if (heightValue >= seaLevel) {
                    // map height to color
                    final float dynamicHeightValue = (heightValue - seaLevel) / heightRange;
                    int grayValue = (int) (255 * dynamicHeightValue); // normalize to 0..255
                    grayValue = Math.min(Math.max(grayValue, 0), 255); // ensure that the value is in the valid range
                    color = new Color(grayValue, grayValue, grayValue);
                } else {
                    // map depth to water color
                    final float dynamicDepthValue = (heightValue - seaLevel) / depthRange;
                    int blueValue = (int) (255 * (dynamicDepthValue)); //  // normalize to 0..255
                    blueValue = Math.min(Math.max(blueValue, 0), 255);
                    color = new Color((int) (blueValue * 0.77), (int) (192 * 0.94), blueValue);
                }

                image.setRGB(x, z, color.getRGB());
            }
        }

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", outputStream);

        return outputStream;
    }

    @NonNull
    public ByteArrayOutputStream generateHeightmap(@NonNull final List<MapTopographyEntity> mapTopographyEntities) throws IOException {
        final int resolution = 1444; // select count(distinct(coordinate x+z)) from map_topography_entity;
//        int x = 0;
//        int z = 0;
        int x = resolution - 1;
        int z = 0;


        final float[][] heightMap = new float[resolution][resolution];
        float maxHeight = 0;
        float maxWaterDepth = 0;
        float seaLevel = 0.0f;

        for (final MapTopographyEntity entity : mapTopographyEntities) {
            final float height = entity.getCoordinateY().floatValue(); // coordinateY = height
            heightMap[x][z] = height;
            if (height > maxHeight) {
                maxHeight = height;
            }
            if (height < maxWaterDepth) {
                maxWaterDepth = height;
            }

            z--;
            if (z < 0) {
                z = resolution - 1;
                if (x == 0) {
                    break; // we are done; we have all the data we need
                } else {
                    x--;
                }
            }
            /*
            z++;
            if (z == resolution) {
                z = 0;
                x++;
            }
             */
        }

        return createHeightMap(seaLevel, maxHeight, maxWaterDepth, heightMap);
    }

}