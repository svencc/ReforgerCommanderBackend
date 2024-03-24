package com.recom.service.map.topography;

import com.recom.commons.model.DEMDescriptor;
import com.recom.entity.map.GameMap;
import com.recom.persistence.map.topography.MapTopographyChunkPersistenceLayer;
import com.recom.service.SerializationService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DEMService {

    @NonNull
    private final MapTopographyChunkPersistenceLayer mapTopographyChunkPersistenceLayer;
    @NonNull
    private final SerializationService serializationService;


    /*
    @NonNull
    public DEMDescriptor deserializeToDEM(@NonNull final List<MapTopography> mapTopography) throws IOException {
        return DEMDescriptor.builder().build();
        // @TODO; muss man aus den Chunks + Meta wieder zusammenbauen!
//        final TopographyData topographyModel = serializationService.<TopographyData>deserializeObject(mapTopography.getData())
//                .orElseThrow(() -> new IOException("Unable to deserialize topography data!"));

//        return invertDEM(topographyModel);
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
                .stepSize(Optional.ofNullable(topograpyModel.getStepSize()).orElseGet(null))
                .scanIterationsX(topograpyModel.getScanIterationsX())
                .scanIterationsZ(topograpyModel.getScanIterationsZ())
                .dem(heightMap)
                .seaLevel(topograpyModel.getOceanBaseHeight())
                .maxHeight(maxHeight)
                .maxWaterDepth(maxWaterDepth)
                .build();
    }
*/

    @NonNull
    public DEMDescriptor invertDEM(
            @NonNull final float[][] dem,
            @NonNull final GameMap gameMap
    ) {
        @NonNull final float[][] heightMap = new float[gameMap.getMapDimensions().getDimensionX().intValue()][gameMap.getMapDimensions().getDimensionZ().intValue()];
        float maxHeight = 0;
        float maxWaterDepth = 0;

        // initialize counter variables
        final int resolutionZ = gameMap.getMapDimensions().getDimensionZ().intValue();
        int counterX = 0;
        int counterZ = resolutionZ - 1;

        // iterate over the surface data and invert the map
        for (final float[] xHeight : dem) {
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
                .stepSize(1)
                .scanIterationsX(gameMap.getMapDimensions().getDimensionX().intValue())
                .scanIterationsZ(gameMap.getMapDimensions().getDimensionZ().intValue())
                .dem(heightMap)
                .seaLevel(gameMap.getMapDimensions().getOceanBaseHeight().floatValue())
                .maxHeight(maxHeight)
                .maxWaterDepth(maxWaterDepth)
                .build();
    }

}