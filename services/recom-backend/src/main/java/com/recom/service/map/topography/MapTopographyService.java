package com.recom.service.map.topography;

import com.recom.commons.map.MapComposer;
import com.recom.commons.map.PixelBufferMapperUtil;
import com.recom.commons.map.rasterizer.interpolation.DEMInterpolationAlgorithm;
import com.recom.commons.map.rasterizer.mapdesignscheme.MapDesignScheme;
import com.recom.commons.map.rasterizer.mapdesignscheme.MapDesignSchemeImplementation;
import com.recom.commons.map.rasterizer.mapdesignscheme.ReforgerMapDesignScheme;
import com.recom.commons.model.DEMDescriptor;
import com.recom.commons.model.maprendererpipeline.MapComposerConfiguration;
import com.recom.commons.model.maprendererpipeline.MapComposerWorkPackage;
import com.recom.commons.model.maprendererpipeline.MapLayerRasterizerConfiguration;
import com.recom.dto.map.mapcomposer.MapComposerConfigurationDto;
import com.recom.entity.map.GameMap;
import com.recom.entity.map.MapTopography;
import com.recom.exception.HttpNotFoundException;
import com.recom.exception.HttpUnprocessableEntityException;
import com.recom.mapper.mapcomposer.MapDesignSchemeMapper;
import com.recom.mapper.mapcomposer.MapLayerRasterizerConfigurationMapper;
import com.recom.persistence.map.topography.MapLocatedTopographyPersistenceLayer;
import com.recom.service.mapentitygenerator.ForestProviderGenerator;
import com.recom.service.SerializationService;
import com.recom.service.mapentitygenerator.StructureProviderGenerator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MapTopographyService {

    @NonNull
    private final MapLocatedTopographyPersistenceLayer mapTopographyPersistenceLayer;
    @NonNull
    private final SerializationService serializationService;
    @NonNull
    private final DEMService demService;
    @NonNull
    private final MapComposer mapComposer;
    @NonNull
    private final DEMInterpolationAlgorithm demInterpolationAlgorithm;
    @NonNull
    private final ForestProviderGenerator forestProviderGenerator;
    @NonNull
    private final StructureProviderGenerator structureProviderGenerator;


    @Transactional(readOnly = true)
    public byte[] provideHeightMapPNG(
            @NonNull final GameMap gameMap,
            @NonNull final Optional<MapComposerConfigurationDto> maybeMapComposerConfiguration
    ) {
        return mapTopographyPersistenceLayer.findByGameMap(gameMap)
                .map(map -> provideTopographyPNG(map, maybeMapComposerConfiguration))
                .orElseThrow(() -> new HttpNotFoundException("No topography com.recom.dto.map found for com.recom.dto.map with id " + gameMap.getId() + "!"));
    }

    @NonNull
    private byte[] provideTopographyPNG(
            @NonNull final MapTopography mapTopography,
            @NonNull final Optional<MapComposerConfigurationDto> maybeMapComposerConfiguration
    ) {
        try {
            final DEMDescriptor demDescriptor = demService.deserializeToDEM(mapTopography);

            final int scaleFactor = provideScaleFactor(maybeMapComposerConfiguration);
            if (scaleFactor > 1) {
                float[][] interpolatedDem = demInterpolationAlgorithm.interpolate(demDescriptor, scaleFactor);
                demDescriptor.setDem(interpolatedDem);
                demDescriptor.setStepSize(calculateStepSize(demDescriptor, (float) scaleFactor));
            }

            final MapComposerWorkPackage workPackage = provideMapComposerWorkPackage(demDescriptor, maybeMapComposerConfiguration);

            mapComposer.registerForestProvider(forestProviderGenerator.generate(mapTopography.getGameMap()));
            mapComposer.registerVillageProvider(structureProviderGenerator.generate(mapTopography.getGameMap()));
            mapComposer.execute(workPackage);

            if (workPackage.getReport().isSuccess()) {
                final int[] composedMap = mapComposer.merge(workPackage);
                final ByteArrayOutputStream composedMapStream = PixelBufferMapperUtil.map(demDescriptor, composedMap);

                // @TODO This is for debugging purposes only; Dirty Hack to put the generated images into the file system here ...
                serializationService.writeBytesToFile(Path.of("cached-composed-map.png"), composedMapStream.toByteArray());

                return composedMapStream.toByteArray();
            } else {
                throw new HttpUnprocessableEntityException();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new HttpUnprocessableEntityException();
        }
    }

    private int calculateStepSize(
            @NonNull final DEMDescriptor demDescriptor,
            float scaleFactor
    ) {
        if (demDescriptor.getStepSize() % scaleFactor != 0) {
            throw new IllegalArgumentException("The provided scale factor does not fit the step size of the dem!");
        } else {
            return (int) (demDescriptor.getStepSize() / scaleFactor);
        }
    }

    private int provideScaleFactor(@NonNull Optional<MapComposerConfigurationDto> maybeMapComposerConfiguration) {
        return maybeMapComposerConfiguration
                .map(MapComposerConfigurationDto::getScaleFactor)
                .orElse(1);
    }

    @NonNull
    private MapComposerWorkPackage provideMapComposerWorkPackage(
            @NonNull final DEMDescriptor demDescriptor,
            @NonNull final Optional<MapComposerConfigurationDto> mapComposerConfiguration
    ) {
        // @TODO: Work here ....
        if (mapComposerConfiguration.isPresent()) {
            final MapDesignScheme designScheme = Optional.ofNullable(mapComposerConfiguration.get().getMapDesignScheme())
                    .map(MapDesignSchemeMapper.INSTANCE::toDesignScheme)
                    .orElseGet(MapDesignSchemeImplementation::new);

            final List<MapLayerRasterizerConfiguration> layerRasterConfigs = Optional.ofNullable(mapComposerConfiguration.get().getRendererConfiguration())
                    .map((configs) -> configs.stream().map(MapLayerRasterizerConfigurationMapper.INSTANCE::toMapLayerRasterizerConfiguration).toList())
                    .orElse(new ArrayList<>());

            // implement conversion of hex values ...
            // I think we have to have default values, which gets overwritten by the provided configuration; test and verify; maybe i have to preset default values in class
            final MapComposerConfiguration configuration = MapComposerConfiguration.builder()
                    .demDescriptor(demDescriptor)
                    .mapDesignScheme(designScheme)
                    .rendererConfiguration(layerRasterConfigs)
                    .build();

            return MapComposerWorkPackage.builder()
                    .mapComposerConfiguration(configuration)
                    .build();
        } else {
            final MapComposerConfiguration defaultConfiguration = MapComposerConfiguration.builder()
                    .demDescriptor(demDescriptor)
                    .mapDesignScheme(new ReforgerMapDesignScheme())
                    .build();

            return MapComposerWorkPackage.builder()
                    .mapComposerConfiguration(defaultConfiguration)
                    .build();
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
