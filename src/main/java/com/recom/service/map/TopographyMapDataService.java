package com.recom.service.map;

import com.recom.entity.GameMap;
import com.recom.entity.MapTopography;
import com.recom.persistence.map.topography.MapTopographyPersistenceLayer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TopographyMapDataService {

    @NonNull
    private final MapTopographyPersistenceLayer mapTopographyPersistenceLayer;
    @NonNull
    private final HeightmapGeneratorService heightmapGeneratorService;


    @Transactional(readOnly = true)
    public byte[] provideTopographyMap(@NonNull final GameMap gameMap) throws IOException {
//        final List<MapTopography> mapTopographyEntities = mapTopographyPersistenceLayer.findAllByMapNameOrdered(gameMap);
        final List<MapTopography> mapTopographyEntities = List.of();

        log.info("Found {} topography entities for map {}", mapTopographyEntities.size(), gameMap);

        final ByteArrayOutputStream outputStream = heightmapGeneratorService.generateHeightmap(mapTopographyEntities);
        final byte[] byteArray = outputStream.toByteArray();

        try (FileOutputStream fileOutputStream = new FileOutputStream(Path.of("heightmap.png").toFile())) {
            fileOutputStream.write(byteArray);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return byteArray;
    }

}
