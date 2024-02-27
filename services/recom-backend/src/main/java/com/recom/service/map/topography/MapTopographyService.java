package com.recom.service.map.topography;

import com.recom.commons.map.MapComposer;
import com.recom.commons.map.PixelBufferMapperUtil;
import com.recom.commons.map.rasterizer.mapdesignscheme.ReforgerMapDesignScheme;
import com.recom.commons.model.DEMDescriptor;
import com.recom.commons.model.maprendererpipeline.MapComposerConfiguration;
import com.recom.commons.model.maprendererpipeline.MapComposerWorkPackage;
import com.recom.entity.map.GameMap;
import com.recom.entity.map.MapTopography;
import com.recom.exception.HttpNotFoundException;
import com.recom.exception.HttpUnprocessableEntityException;
import com.recom.persistence.map.topography.MapLocatedTopographyPersistenceLayer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MapTopographyService {

    @NonNull
    private final MapLocatedTopographyPersistenceLayer mapTopographyPersistenceLayer;
    @NonNull
    private final DEMService demService;
    @NonNull
    private final MapComposer mapComposer;


    @Transactional(readOnly = true)
    public byte[] provideHeightMapPNG(@NonNull final GameMap gameMap) {
        return mapTopographyPersistenceLayer.findByGameMap(gameMap)
                .map(this::provideTopographyPNG)
                .orElseThrow(() -> new HttpNotFoundException("No topography com.recom.dto.map found for com.recom.dto.map with id " + gameMap.getId() + "!"));
    }

    @NonNull
    private byte[] provideTopographyPNG(@NonNull final MapTopography mapTopography) {
        try {
            final DEMDescriptor demDescriptor = demService.deserializeToDEM(mapTopography);
            final MapComposerWorkPackage workPackage = MapComposerWorkPackage.builder()
                    .mapComposerConfiguration(
                            MapComposerConfiguration.builder()
                                    .demDescriptor(demDescriptor)
                                    .mapDesignScheme(new ReforgerMapDesignScheme())
                                    .build()
                    )
                    .build();

            mapComposer.execute(workPackage);

            if (workPackage.getReport().isSuccess()) {
//            if (workPackage.getReport().isSuccess() && (workPackage.getPipelineArtifacts().getArtifactFrom(HeightMapRasterizer.class).isPresent())) {
//                final int[] heightMapRasterizerArtifact = workPackage.getPipelineArtifacts().getArtifactFrom(HeightMapRasterizer.class).get().getData();
//                final ByteArrayOutputStream outputStream = PixelBufferMapperUtil.map(demDescriptor, heightMapRasterizerArtifact);


                // @TODO -> extract to helper function!
                // write the composed map to the file system ------------- ------------- ------------- ------------- -------------
                final int[] composedMap = mapComposer.merge(workPackage);
                final ByteArrayOutputStream composedMapStream = PixelBufferMapperUtil.map(demDescriptor, composedMap);
//                serializationService.writeBytesToFile(Path.of("cached-composed-map.png"), composedMapStream.toByteArray());
                // ------------- ------------- ------------- ------------- ------------- ------------- ------------- -------------


//                return outputStream.toByteArray();
                return composedMapStream.toByteArray();
            } else {
                throw new HttpUnprocessableEntityException();
            }





            /*
            final ByteArrayOutputStream outputStream = mapPNGGeneratorService.generateHeightmapPNG(demDescriptor);
            final ByteArrayOutputStream outputStreamShade = mapPNGGeneratorService.generateShadeMapPNG(demDescriptor);
            final ByteArrayOutputStream outputStreamContour = mapPNGGeneratorService.generateContourMapPNG(demDescriptor);
            final ByteArrayOutputStream outputStreamSlope = mapPNGGeneratorService.generateSlopeMapPNG(demDescriptor);
            final byte[] heightmapByteArray = outputStream.toByteArray();

            // @TODO This is for debugging purposes only; Dirty Hack to put the generated images into the file system here ...
            serializationService.writeBytesToFile(Path.of("cached-heightmap.png"), heightmapByteArray); // TODO: Remove OR make configurable!
            serializationService.writeBytesToFile(Path.of("cached-shademap.png"), outputStreamShade.toByteArray()); // TODO: Remove OR make configurable!
            serializationService.writeBytesToFile(Path.of("cached-contourmap.png"), outputStreamContour.toByteArray()); // TODO: Remove OR make configurable!
            serializationService.writeBytesToFile(Path.of("cached-slopemap.png"), outputStreamSlope.toByteArray()); // TODO: Remove OR make configurable!
            return heightmapByteArray;
            */
        } catch (Exception e) {
            throw new HttpUnprocessableEntityException();
        }
    }


    @NonNull
    public Optional<DEMDescriptor> provideDEMDescriptor(@NonNull final GameMap gameMap) {
        return mapTopographyPersistenceLayer.findByGameMap(gameMap)
                .map(mapTopography -> {
                    try {
                        return demService.deserializeToDEM(mapTopography);
                    } catch (IOException e) {
                        throw new HttpUnprocessableEntityException();
                    }
                });
    }

}
