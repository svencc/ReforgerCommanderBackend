package com.recom.service.map.topography;

import com.recom.commons.model.DEMDescriptor;
import com.recom.entity.map.GameMap;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class DEMInvertService {

    @NonNull
    public DEMDescriptor invertDEM(
            @NonNull final float[][] dem,
            @NonNull final GameMap gameMap
    ) {
        try {
            int demWidth = gameMap.getMapDimensions().getDimensionX().intValue();
            int demHeight = gameMap.getMapDimensions().getDimensionZ().intValue();
            final float[][] heightMap = new float[demHeight][demWidth];
            float maxHeight = 0;
            float maxWaterDepth = 0;

            // initialize counter variables
            final int resolutionZ = gameMap.getMapDimensions().getDimensionZ().intValue();
            int counterX = 0;
            int counterZ = resolutionZ - 1;

            // iterate over the surface data and invert the map
            for (final float[] xHeight : dem) {
                for (final float height : xHeight) {
                    heightMap[counterZ][counterX] = height;
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
                    .stepSize(BigDecimal.ONE) // guessing just one
                    .dem(heightMap)
                    .seaLevel(gameMap.getMapDimensions().getOceanBaseHeight().floatValue())
                    .maxHeight(maxHeight)
                    .maxWaterDepth(maxWaterDepth)
                    .build();
        } catch (final Throwable t) {
            log.error("Error inverting DEM", t);
            throw t;
        }
    }

}