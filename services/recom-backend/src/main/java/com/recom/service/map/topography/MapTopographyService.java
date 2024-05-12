package com.recom.service.map.topography;

import com.recom.commons.map.MapComposer;
import com.recom.commons.map.PixelBufferMapperUtil;
import com.recom.commons.map.rasterizer.interpolation.DEMDownscaleAlgorithm;
import com.recom.commons.map.rasterizer.interpolation.DEMUpscaleAlgorithmBilinear;
import com.recom.commons.map.rasterizer.interpolation.PixelScaler;
import com.recom.commons.map.rasterizer.mapdesignscheme.MapDesignScheme;
import com.recom.commons.map.rasterizer.mapdesignscheme.MapDesignSchemeImplementation;
import com.recom.commons.map.rasterizer.mapdesignscheme.ReforgerMapDesignScheme;
import com.recom.commons.model.DEMDescriptor;
import com.recom.commons.model.maprendererpipeline.MapComposerConfiguration;
import com.recom.commons.model.maprendererpipeline.MapComposerWorkPackage;
import com.recom.commons.model.maprendererpipeline.MapLayerRasterizerConfiguration;
import com.recom.dto.map.mapcomposer.MapComposerConfigurationDto;
import com.recom.entity.map.GameMap;
import com.recom.entity.map.MapDimensions;
import com.recom.entity.map.SquareKilometerTopographyChunk;
import com.recom.exception.HttpUnprocessableEntityException;
import com.recom.mapper.mapcomposer.MapDesignSchemeMapper;
import com.recom.mapper.mapcomposer.MapLayerRasterizerConfigurationMapper;
import com.recom.persistence.map.chunk.topography.MapTopographyChunkPersistenceLayer;
import com.recom.service.SerializationService;
import com.recom.service.mapentitygenerator.ForestProviderGenerator;
import com.recom.service.mapentitygenerator.StructureProviderGenerator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MapTopographyService {

    @NonNull
    private final SerializationService serializationService;
    @NonNull
    private final DEMInvertService demInvertService;
    @NonNull
    private final MapComposer mapComposer;
    @NonNull
    private final DEMUpscaleAlgorithmBilinear demUpscaleAlgorithm;
//    @NonNull
//    private final PixelScaler pixelScaler;
    @NonNull
    private final DEMDownscaleAlgorithm demDownscaleAlgorithm;
    @NonNull
    private final ForestProviderGenerator forestProviderGenerator;
    @NonNull
    private final StructureProviderGenerator structureProviderGenerator;
    @NonNull
    private final MapTopographyChunkPersistenceLayer mapTopographyPersistenceLayer;
    @NonNull
    private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
    @NonNull
    private final StepSizeCalculator stepSizeCalculator;


    @Transactional(readOnly = true)
    public byte[] provideMapPNG(
            @NonNull final GameMap gameMap,
            @NonNull final Optional<MapComposerConfigurationDto> maybeMapComposerConfiguration
    ) {
        return provideTopographyPNG(gameMap, provideDEMDescriptor(gameMap), maybeMapComposerConfiguration);
    }

    @NonNull
    private byte[] provideTopographyPNG(
            @NonNull final GameMap gameMap,
            @NonNull final DEMDescriptor demDescriptor,
            @NonNull final Optional<MapComposerConfigurationDto> maybeMapComposerConfiguration
    ) {
        try {
            final BigDecimal scaleFactor = provideScaleFactor(maybeMapComposerConfiguration);
            if (scaleFactor.compareTo(BigDecimal.ONE) > 0) {
                float[][] interpolatedDem = demUpscaleAlgorithm.scaleUp(demDescriptor, scaleFactor.intValue());
//                float[][] interpolatedDem = pixelScaler.scale(demDescriptor, scaleFactor.intValue());
                demDescriptor.setDem(interpolatedDem);
                demDescriptor.setStepSize(stepSizeCalculator.calculateStepSize(demDescriptor, new BigDecimal(scaleFactor.intValue())));
            } else if (scaleFactor.compareTo(BigDecimal.ONE) < 0) {
                float[][] downScaledDem = demDownscaleAlgorithm.scaleDown(demDescriptor, scaleFactor.multiply(new BigDecimal(100)).intValue());
//                float[][] downScaledDem = pixelScaler.scale(demDescriptor, scaleFactor.intValue());
                demDescriptor.setDem(downScaledDem);
                demDescriptor.setStepSize(stepSizeCalculator.calculateStepSize(demDescriptor, scaleFactor));
            }

            final MapComposerWorkPackage workPackage = provideMapComposerWorkPackage(demDescriptor, maybeMapComposerConfiguration);

            mapComposer.registerForestProvider(forestProviderGenerator.provideFutureGenerator(gameMap));
            mapComposer.registerVillageProvider(structureProviderGenerator.provideFutureGenerator(gameMap));
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
        } catch (final Throwable t) {
            log.error(t.getMessage(), t);
            throw new HttpUnprocessableEntityException();
        }
    }

    private BigDecimal provideScaleFactor(@NonNull Optional<MapComposerConfigurationDto> maybeMapComposerConfiguration) {
        return maybeMapComposerConfiguration
                .map(MapComposerConfigurationDto::getScaleFactor)
                .orElse(BigDecimal.ONE);
    }

    @NonNull
    private MapComposerWorkPackage provideMapComposerWorkPackage(
            @NonNull final DEMDescriptor demDescriptor,
            @NonNull final Optional<MapComposerConfigurationDto> mapComposerConfiguration
    ) {
        if (mapComposerConfiguration.isPresent()) {
            final MapDesignScheme designScheme = Optional.ofNullable(mapComposerConfiguration.get().getMapDesignScheme())
                    .map(MapDesignSchemeMapper.INSTANCE::toDesignScheme)
                    .orElseGet(MapDesignSchemeImplementation::new);

            final List<MapLayerRasterizerConfiguration> layerRasterConfigs = Optional.ofNullable(mapComposerConfiguration.get().getRendererConfiguration())
                    .map((configs) -> configs.stream().map(MapLayerRasterizerConfigurationMapper.INSTANCE::toMapLayerRasterizerConfiguration).toList())
                    .orElse(new ArrayList<>());

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
    public DEMDescriptor provideDEMDescriptor(@NonNull final GameMap gameMap) {
        final List<SquareKilometerTopographyChunk> chunks = mapTopographyPersistenceLayer.findByGameMap(gameMap);

        final MapDimensions mapDimensions = gameMap.getMapDimensions();
        final int mapHeightX = mapDimensions.getDimensionX().intValue();
        final int mapWidthY = mapDimensions.getDimensionZ().intValue();

        final float[][] dem = new float[mapHeightX][mapWidthY];
        final CountDownLatch latch = new CountDownLatch(chunks.size());
        for (final SquareKilometerTopographyChunk chunk : chunks) {
            executorService.execute(() -> {
                try {
                    final Optional<float[][]> maybeChunkedDEM = serializationService.deserializeObject(chunk.getData());
                    if (maybeChunkedDEM.isPresent()) {
                        final int demGridOffsetX = chunk.getSquareCoordinateX().intValue() * 1000;
                        final int demGridOffsetY = chunk.getSquareCoordinateY().intValue() * 1000;

                        final float[][] chunkedDem = maybeChunkedDEM.get();
                        for (int x = 0; x < chunkedDem.length; x++) {
                            final int demCoordinateX = demGridOffsetX + x;
                            if (demCoordinateX >= mapHeightX) {
                                break;
                            }
                            for (int y = 0; y < chunkedDem[x].length; y++) {
                                final int demCoordinateY = demGridOffsetY + y;
                                if (demCoordinateY >= mapWidthY) {
                                    break;
                                }
                                dem[demCoordinateX][demCoordinateY] = chunkedDem[x][y];
                            }
                        }
                    }
                } catch (final Throwable e) {
                    log.error("{}: {}\n{}", getClass().getName(), e.getMessage(), e.getStackTrace());
                } finally {
                    latch.countDown();
                }
            });
        }

        try {
            latch.await();
        } catch (final InterruptedException e) {
            log.error("{}: {}\n{}", getClass().getName(), e.getMessage(), e.getStackTrace());
        }

        try {
            return demInvertService.invertDEM(dem, gameMap);
        } catch (Throwable e) {
            log.error("{}: {}\n{}", getClass().getName(), e.getMessage(), e.getStackTrace());
            throw new HttpUnprocessableEntityException();
        }
    }

}
