package com.recom.service.map;

import com.recom.entity.GameMap;
import com.recom.entity.MapTopography;
import com.recom.exception.HttpNotFoundException;
import com.recom.persistence.map.topography.MapLocatedTopographyPersistenceLayer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
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


    @Transactional(readOnly = true)
    public byte[] provideTopographyMap(@NonNull final GameMap gameMap) throws IOException {
        final Optional<MapTopography> maybeMapTopography = mapTopographyPersistenceLayer.findByGameMap(gameMap);

        if (maybeMapTopography.isEmpty()) {
            throw new HttpNotFoundException("No topography map found for map with id " + gameMap.getId() + "!");
        }

        final ByteArrayOutputStream outputStream = heightmapGeneratorService.generateHeightmap(maybeMapTopography.get());
        final byte[] byteArray = outputStream.toByteArray();

        try (FileOutputStream fileOutputStream = new FileOutputStream(Path.of("heightmap.png").toFile())) {
            fileOutputStream.write(byteArray);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return byteArray;
    }

}
