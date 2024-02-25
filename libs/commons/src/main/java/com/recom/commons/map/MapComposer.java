package com.recom.commons.map;

import com.recom.commons.map.rasterizer.*;
import com.recom.commons.map.rasterizer.configuration.MapLayerRenderer;
import com.recom.commons.model.maprendererpipeline.MapComposerWorkPackage;
import com.recom.commons.model.maprendererpipeline.MapLayerRendererConfiguration;
import com.recom.commons.model.maprendererpipeline.report.PipelineLogMessage;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.MissingRequiredPropertiesException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Slf4j
@NoArgsConstructor
public class MapComposer {

    @NonNull
    private final Map<String, MapLayerRenderer> mapLayerRendererPipeline = new HashMap<>();
    @NonNull
    private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();


    @NonNull
    public static MapComposer withDefaultConfiguration() {
        final MapComposer mapComposer = new MapComposer();

        mapComposer.registerRenderer(new SlopeAndAspectMapRasterizer());
        mapComposer.registerRenderer(new HeightMapRasterizer());
        mapComposer.registerRenderer(new ShadowedMapRasterizer());
        mapComposer.registerRenderer(new ContourMapRasterizer());
        mapComposer.registerRenderer(new SlopeMapRasterizer());

        return mapComposer;
    }

    public void registerRenderer(@NonNull final MapLayerRenderer renderer) {
        mapLayerRendererPipeline.put(renderer.getClass().getSimpleName(), renderer);
    }

    @NonNull
    public ByteArrayOutputStream compose(@NonNull final MapComposerWorkPackage workPackage) throws MissingRequiredPropertiesException {
        applyConfigurationToRenderer(workPackage);

        final MapComposerWorkPackage completedCoreDataWorkPackage = renderCoreDataInSequence(workPackage);
        final MapComposerWorkPackage completedWorkPackage = renderDataInParallel(workPackage);

        handleException(workPackage);


        // @TODO
        // Problem ist jetzt dass ich die buffer; nicht die image ByteStreams brauche; plus in der richtigen reihenfolge!
        // diese infos sind ja im renderer drin, brauche das in der Ergebnissliste auch -> übertragung von renderer zu pipelineArtefacts


        // -------------------------------------------------------------------------------------------------------------
        // prepare pixel buffer
        final int width = completedWorkPackage.getMapComposerConfiguration().getDemDescriptor().getDemWidth();
        final int height = completedWorkPackage.getMapComposerConfiguration().getDemDescriptor().getDemHeight();
        final int[] pixelBuffer = new int[width * height];
        // ---


        // @TODO 1. extract to common method to write buffered pixels to image
        // @TODO 2. extract to common method write image to file
        // write buffered pixels to image
        final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        final int[] imagePixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(pixelBuffer, 0, imagePixels, 0, pixelBuffer.length);

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // ---

        return outputStream;
    }

    @NonNull
    private MapComposerWorkPackage renderDataInParallel(@NonNull final MapComposerWorkPackage workPackage) {
        return mapLayerRendererPipeline.values().stream()
                .sorted(Comparator.comparingInt((final MapLayerRenderer mapLayerRenderer) -> mapLayerRenderer.getMapLayerRendererConfiguration().getLayerOrder().getOrder()))
                .filter((final MapLayerRenderer renderer) -> renderer.getMapLayerRendererConfiguration().isEnabled())
                .filter((final MapLayerRenderer renderer) -> !renderer.getMapLayerRendererConfiguration().isSequentialCoreData())
                .map(renderer -> CompletableFuture.supplyAsync(() -> {
                    try {
                        renderer.render(workPackage);
                    } catch (final IOException e) {
                        workPackage.getReport().logException(e);
                    }

                    return workPackage;
                }, executorService))
                .toList().stream() // terminate task creation
                .map(CompletableFuture::join)
                .reduce(workPackage, (wrkPackage, b) -> wrkPackage, (wrkPackage, b) -> wrkPackage);
    }

    @NonNull
    private MapComposerWorkPackage renderCoreDataInSequence(@NonNull final MapComposerWorkPackage workPackage) {
        return mapLayerRendererPipeline.values().stream()
                .sorted(Comparator.comparingInt((final MapLayerRenderer mapLayerRenderer) -> mapLayerRenderer.getMapLayerRendererConfiguration().getLayerOrder().getOrder()))
                .filter((final MapLayerRenderer renderer) -> renderer.getMapLayerRendererConfiguration().isEnabled())
                .filter((final MapLayerRenderer renderer) -> renderer.getMapLayerRendererConfiguration().isSequentialCoreData())
                .peek(renderer -> {
                    try {
                        renderer.render(workPackage);
                    } catch (final IOException e) {
                        workPackage.getReport().logException(e);
                    }
                })
                .reduce(workPackage, (wrkPackage, b) -> wrkPackage, (wrkPackage, b) -> wrkPackage);
    }

    private void handleException(@NonNull final MapComposerWorkPackage workPackage) throws MissingRequiredPropertiesException {
        if (!workPackage.getReport().isSuccess()) {
            workPackage.getReport().getMessages().stream()
                    .map(PipelineLogMessage::toString)
                    .forEach((log::error));
            throw new MissingRequiredPropertiesException();
        }
    }

    private void applyConfigurationToRenderer(@NonNull final MapComposerWorkPackage workPackage) {
        workPackage.getMapComposerConfiguration().getRendererConfiguration().entrySet().stream()
                .filter(entry -> mapLayerRendererPipeline.containsKey(entry.getKey()))
                .forEach(configurationEntry -> {
                    final String layerRendererName = configurationEntry.getKey();
                    final MapLayerRendererConfiguration configuration = configurationEntry.getValue();

                    final MapLayerRenderer mapLayerRenderer = mapLayerRendererPipeline.get(layerRendererName);
                    configuration.applyConfiguration(mapLayerRenderer);
                });
    }

}