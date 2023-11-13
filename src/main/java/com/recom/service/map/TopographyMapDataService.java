package com.recom.service.map;

import com.recom.entity.MapTopographyEntity;
import com.recom.persistence.mapTopography.MapTopographyEntityPersistenceLayer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TopographyMapDataService {

    @NonNull
    private final MapTopographyEntityPersistenceLayer mapTopographyEntityPersistenceLayer;
    @NonNull
    private final HeightmapGeneratorService heightmapGeneratorService;


    @Transactional(readOnly = true)
    public byte[] provideTopographyMap(@NonNull final String mapName) throws IOException {
        final List<MapTopographyEntity> mapTopographyEntities = mapTopographyEntityPersistenceLayer.findAllByMapName(mapName);

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
