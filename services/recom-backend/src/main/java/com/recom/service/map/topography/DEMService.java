package com.recom.service.map.topography;

import com.recom.commons.model.DEMDescriptor;
import com.recom.entity.map.MapTopography;
import com.recom.model.map.TopographyData;
import com.recom.service.SerializationService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DEMService {

    @NonNull
    private final SerializationService serializationService;


    @NonNull
    public DEMDescriptor deserializeToDEM(@NonNull final MapTopography mapTopography) throws IOException {
        final TopographyData topographyModel = serializationService.<TopographyData>deserializeObject(mapTopography.getData())
                .orElseThrow(() -> new IOException("Unable to deserialize topography data!"));

        return invertDEM(topographyModel);
    }

    @NonNull
    private DEMDescriptor invertDEM(@NonNull final TopographyData topograpyModel) {
        @NonNull final float[][] heightMap = new float[topograpyModel.getScanIterationsX()][topograpyModel.getScanIterationsZ()];
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

        return DEMDescriptor.builder()
                .stepSize(Optional.ofNullable(topograpyModel.getStepSize()).map(Float::intValue).orElseGet(null))
                .scanIterationsX(topograpyModel.getScanIterationsX())
                .scanIterationsZ(topograpyModel.getScanIterationsZ())
                .dem(heightMap)
                .seaLevel(topograpyModel.getOceanBaseHeight())
                .maxHeight(maxHeight)
                .maxWaterDepth(maxWaterDepth)
                .build();
    }

}