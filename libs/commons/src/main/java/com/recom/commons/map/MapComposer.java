package com.recom.commons.map;

import com.recom.commons.calculator.ARGBCalculator;
import com.recom.commons.map.rasterizer.*;
import com.recom.commons.map.rasterizer.configuration.MapLayerRenderer;
import com.recom.commons.model.maprendererpipeline.MapComposerWorkPackage;
import com.recom.commons.model.maprendererpipeline.MapLayerRendererConfiguration;
import com.recom.commons.model.maprendererpipeline.report.PipelineLogMessage;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.MissingRequiredPropertiesException;

import java.io.IOException;
import java.util.*;
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
    private final ARGBCalculator argbCalculator = new ARGBCalculator();


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
    public void execute(@NonNull final MapComposerWorkPackage workPackage) throws MissingRequiredPropertiesException {
        applyConfigurationToRenderer(workPackage);

        renderCoreDataInSequence(workPackage);
        renderDataInParallel(workPackage);

        handleException(workPackage);
    }

    @NonNull
    private void renderDataInParallel(@NonNull final MapComposerWorkPackage workPackage) {
        mapLayerRendererPipeline.values().stream()
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
    private void renderCoreDataInSequence(@NonNull final MapComposerWorkPackage workPackage) {
        mapLayerRendererPipeline.values().stream()
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

    @NonNull
    public int[] merge(@NonNull final MapComposerWorkPackage workPackage) {
        final int width = workPackage.getMapComposerConfiguration().getDemDescriptor().getDemWidth();
        final int height = workPackage.getMapComposerConfiguration().getDemDescriptor().getDemHeight();
        final int[] pixelBuffer = new int[width * height];
        Arrays.fill(pixelBuffer, 0xff000000); // prefill with black

        return merge(workPackage, pixelBuffer);
    }

    @NonNull
    @SuppressWarnings("unchecked")
    public int[] merge(
            @NonNull final MapComposerWorkPackage workPackage,
            @NonNull final int[] pixelBuffer
    ) {
        return workPackage.getPipelineArtifacts().getArtifacts().entrySet().stream()
                .sorted(Comparator.comparingInt((entry) -> entry.getValue().getCreator().getMapLayerRendererConfiguration().getLayerOrder().getOrder()))
                .filter(entry -> {
                    final MapLayerRenderer artifactCreator = entry.getValue().getCreator();
                    return artifactCreator.getMapLayerRendererConfiguration().isEnabled()
                            && artifactCreator.getMapLayerRendererConfiguration().isVisible();
                })
                .map(entry -> {
                    final Object artifactRawData = entry.getValue().getData();
                    if (artifactRawData instanceof int[] artifact) {
                        return artifact;
                    } else {
                        log.debug("Artifact of type {} is not supported", artifactRawData.getClass().getSimpleName());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .reduce(pixelBuffer, (targetBuffer, artifactBuffer) -> {
                    for (int i = 0; i < targetBuffer.length; i++) {
                        targetBuffer[i] = argbCalculator.blend(artifactBuffer[i], targetBuffer[i]);
                    }

                    return targetBuffer;
                });
    }

}