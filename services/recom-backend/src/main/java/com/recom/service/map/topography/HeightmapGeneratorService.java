package com.recom.service.map.topography;

import com.recom.entity.MapTopography;
import com.recom.model.HeightMapDescriptor;
import com.recom.model.map.TopographyData;
import com.recom.service.SerializationService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class HeightmapGeneratorService {

    @NonNull
    private final SerializationService serializationService;


    @NonNull
    public ByteArrayOutputStream generateHeightmap(@NonNull final MapTopography mapTopography) throws IOException {
        return createHeightMap(provideHeightmapData(mapTopography));
    }

    @NonNull
    public HeightMapDescriptor provideHeightmapData(@NonNull final MapTopography mapTopography) throws IOException {
        final TopographyData topographyModel = serializationService.<TopographyData>deserializeObject(mapTopography.getData())
                .orElseThrow(() -> new IOException("Unable to deserialize topography data!"));

        return invertHeightmapData(topographyModel);
    }


    @NonNull
    public HeightMapDescriptor invertHeightmapData(@NonNull final TopographyData topograpyModel) {
        final float[][] heightMap = new float[topograpyModel.getScanIterationsX()][topograpyModel.getScanIterationsZ()];
        float maxHeight = 0;
        float maxWaterDepth = 0;

        // initialize counter variables
        final int resolutionZ = topograpyModel.getScanIterationsZ();
        int counterX = 0;
        int counterZ = resolutionZ - 1;

        // iterate over the surface data and invert the com.recom.dto.map
        for (final float[] xHeight : topograpyModel.getSurfaceData()) {
            for (final float height : xHeight) {
                heightMap[counterX][counterZ] = height;
                if (height > maxHeight) {
                    maxHeight = height;
                }
                if (height < maxWaterDepth) {
                    maxWaterDepth = height;
                }

                counterZ--;
                if (counterZ < 0) {
                    counterZ = resolutionZ - 1;
                    if (counterX == resolutionZ - 1) {
                        break; // we are done; we have all the data we need
                    } else {
                        counterX++;
                    }
                }
            }
        }

        return HeightMapDescriptor.builder()
                .stepSize(topograpyModel.getStepSize())
                .scanIterationsX(topograpyModel.getScanIterationsX())
                .scanIterationsZ(topograpyModel.getScanIterationsZ())
                .heightMap(heightMap)
                .seaLevel(topograpyModel.getOceanBaseHeight())
                .maxHeight(maxHeight)
                .maxWaterDepth(maxWaterDepth)
                .build();
    }

    @NonNull
    private ByteArrayOutputStream createHeightMap(@NonNull final HeightMapDescriptor command) throws IOException {
        final int width = command.getHeightMap().length;
        final int height = command.getHeightMap()[0].length;
        final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        final float heightRange = command.getMaxHeight() - command.getSeaLevel();
        final float depthRange = command.getMaxWaterDepth() - command.getSeaLevel();

        for (int x = 0; x < width; x++) {
            for (int z = 0; z < height; z++) {
                final float heightValue = command.getHeightMap()[x][z];
                Color color;

                if (heightValue >= command.getSeaLevel()) {
                    // com.recom.dto.map height to color
                    final float dynamicHeightValue = (heightValue - command.getSeaLevel()) / heightRange;
                    int grayValue = (int) (255 * dynamicHeightValue); // normalize to 0..255
                    grayValue = Math.min(Math.max(grayValue, 0), 255); // ensure that the value is in the valid range
                    color = new Color(grayValue, grayValue, grayValue);
                } else {
                    // com.recom.dto.map depth to water color
                    final float dynamicDepthValue = (heightValue - command.getSeaLevel()) / depthRange;
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

}