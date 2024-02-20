package com.recom.service.map.topography;

import com.recom.entity.map.GameMap;
import com.recom.entity.map.MapTopography;
import com.recom.exception.HttpNotFoundException;
import com.recom.exception.HttpUnprocessableEntityException;
import com.recom.commons.rasterizer.HeightMapDescriptor;
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
    public byte[] provideTopographyPNG(@NonNull final GameMap gameMap) {
        return mapTopographyPersistenceLayer.findByGameMap(gameMap)
                .map(this::provideTopographyPNG)
                .orElseThrow(() -> new HttpNotFoundException("No topography com.recom.dto.map found for com.recom.dto.map with id " + gameMap.getId() + "!"));
    }

    @Transactional(readOnly = true)
    public byte[] provideTopographyPNG(@NonNull final MapTopography mapTopography) {
        try {
            final ByteArrayOutputStream outputStream = heightmapGeneratorService.generateHeightmapPNG(mapTopography);
            final ByteArrayOutputStream outputStreamShade = heightmapGeneratorService.generateShadeMapPNG(mapTopography);
            final ByteArrayOutputStream outputStreamContour = heightmapGeneratorService.generateContourMapPNG(mapTopography);
            final byte[] heightmapByteArray = outputStream.toByteArray();

            // @TODO This is for debugging purposes only; Dirty Hack to put the generated images into the file system here ...
            serializationService.writeBytesToFile(Path.of("cached-heightmap.png"), heightmapByteArray); // TODO: Remove OR make configurable!
            serializationService.writeBytesToFile(Path.of("cached-shademap.png"), outputStreamShade.toByteArray()); // TODO: Remove OR make configurable!
            serializationService.writeBytesToFile(Path.of("cached-contourmap.png"), outputStreamContour.toByteArray()); // TODO: Remove OR make configurable!

            return heightmapByteArray;
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
