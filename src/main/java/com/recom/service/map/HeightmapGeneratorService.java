package com.recom.service.map;

import com.recom.entity.MapTopography;
import com.recom.exception.DBCachedDeserializationException;
import com.recom.model.map.TopographyData;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Optional;

@Slf4j
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
    public ByteArrayOutputStream generateHeightmap(@NonNull final MapTopography mapTopography) throws IOException {
        byte[] data = mapTopography.getData();
        final Optional<TopographyData> topograpyModel = deserializeCacheValue(data);

        final int resolution = topograpyModel.get().getScanIterationsZ();
        int x = 0;
        int z = resolution - 1;


        final float[][] heightMap = new float[resolution][resolution];
        float maxHeight = 0;
        float maxWaterDepth = 0;
        float seaLevel = 0.0f;

        for (final float[] xHeight : topograpyModel.get().getSurfaceData()) {
            for (final float height : xHeight) {
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
                    if (x == resolution - 1) {
                        break; // we are done; we have all the data we need
                    } else {
                        x++;
                    }
                }
            }
        }

        return createHeightMap(seaLevel, maxHeight, maxWaterDepth, heightMap);
    }

    //    @TODO: SERIALIZE + DESERIALIZE in extra Service
    @NonNull
    private <V extends Serializable> Optional<V> deserializeCacheValue(
            final byte[] serializedValue
    ) throws DBCachedDeserializationException {
        try (ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(serializedValue))) {
            return Optional.ofNullable((V) inputStream.readObject());
        } catch (final IOException | ClassNotFoundException e) {
            throw new DBCachedDeserializationException("Unable to deserialize", e);
        }
    }

}