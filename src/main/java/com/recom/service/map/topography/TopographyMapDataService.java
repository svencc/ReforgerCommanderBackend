package com.recom.service.map.topography;

import com.recom.entity.GameMap;
import com.recom.entity.MapTopography;
import com.recom.exception.HttpNotFoundException;
import com.recom.exception.HttpUnprocessableEntityException;
import com.recom.model.HeightMapDescriptor;
import com.recom.persistence.map.topography.MapLocatedTopographyPersistenceLayer;
import com.recom.service.SerializationService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TopographyMapDataService {

    @NonNull
    private final MapLocatedTopographyPersistenceLayer mapTopographyPersistenceLayer;
    @NonNull
    private final HeightmapGeneratorService heightmapGeneratorService;
    @NonNull
    private final SerializationService serializationService;


    @Transactional(readOnly = true)
    public byte[] provideTopographyMap(@NonNull final GameMap gameMap) {
        return mapTopographyPersistenceLayer.findByGameMap(gameMap)
                .map(mapTopography -> provideTopographyMap(mapTopography))
                .orElseThrow(() -> new HttpNotFoundException("No topography map found for map with id " + gameMap.getId() + "!"));
    }

    @Transactional(readOnly = true)
    public byte[] provideTopographyMap(@NonNull final MapTopography mapTopography) {
        try {
            final ByteArrayOutputStream outputStream = heightmapGeneratorService.generateHeightmap(mapTopography);
            final byte[] byteArray = outputStream.toByteArray();

            serializationService.writeBytesToFile(Path.of("cached-heightmap.png"), byteArray);

            return byteArray;
        } catch (IOException e) {
            throw new HttpUnprocessableEntityException();
        }
    }


    @NonNull
    public Optional<HeightMapDescriptor> provideTopographyData(@NonNull final GameMap gameMap) {
        return mapTopographyPersistenceLayer.findByGameMap(gameMap)
                .map(mapTopography -> {
                    try {
                        return heightmapGeneratorService.provideHeightmapData(mapTopography);
                    } catch (IOException e) {
                        throw new HttpUnprocessableEntityException();
                    }
                });
    }
}
