package com.recom.service.map.topography;

import com.recom.commons.rasterizer.HeightMapDescriptor;
import com.recom.commons.rasterizer.HeightmapRasterizer;
import com.recom.entity.map.MapTopography;
import com.recom.model.map.TopographyData;
import com.recom.service.SerializationService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class HeightmapGeneratorService {

    @NonNull
    private final SerializationService serializationService;
    @NonNull
    private final HeightmapRasterizer heightmapRasterizer;


    @NonNull
    public ByteArrayOutputStream generateHeightmapPNG(@NonNull final MapTopography mapTopography) throws IOException {
        return heightmapRasterizer.rasterizeHeightMapPNG(provideHeightmapData(mapTopography));
    }

    @NonNull
    public HeightMapDescriptor provideHeightmapData(@NonNull final MapTopography mapTopography) throws IOException {
        final TopographyData topographyModel = serializationService.<TopographyData>deserializeObject(mapTopography.getData())
                .orElseThrow(() -> new IOException("Unable to deserialize topography data!"));

        return invertHeightmapData(topographyModel);
    }

    @NonNull
    private HeightMapDescriptor invertHeightmapData(@NonNull final TopographyData topograpyModel) {
        final float[][] heightMap = new float[topograpyModel.getScanIterationsX()][topograpyModel.getScanIterationsZ()];
        float maxHeight = 0;
        float maxWaterDepth = 0;

        // initialize counter variables
        final int resolutionZ = topograpyModel.getScanIterationsZ();
        int counterX = 0;
        int counterZ = resolutionZ - 1;

        // iterate over the surface data and invert the map
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
    public ByteArrayOutputStream generateShadeMapPNG(@NonNull final MapTopography mapTopography) {
        try {
            return heightmapRasterizer.rasterizeShadeMap(provideHeightmapData(mapTopography));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @NonNull
    public ByteArrayOutputStream generateContourMapPNG(@NonNull final MapTopography mapTopography) {
        try {
            return heightmapRasterizer.rasterizeContourMap(provideHeightmapData(mapTopography));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @NonNull
    public ByteArrayOutputStream generateSlopeMapPNG(@NonNull final MapTopography mapTopography) {
        try {
            return heightmapRasterizer.rasterizeSlopeMap(provideHeightmapData(mapTopography));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}