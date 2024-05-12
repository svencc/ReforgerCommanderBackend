package com.recom.commons.map;

import com.recom.commons.calculator.ARGBCalculator;
import com.recom.commons.map.rasterizer.batch0.ForestClusterCreator;
import com.recom.commons.map.rasterizer.batch1.SlopeAndAspectMapRasterizer;
import com.recom.commons.map.rasterizer.batch0.StructureClusterCreator;
import com.recom.commons.map.rasterizer.batch2.*;
import com.recom.commons.map.rasterizer.configuration.MapLayerRasterizer;
import com.recom.commons.model.maprendererpipeline.MapComposerWorkPackage;
import com.recom.commons.model.maprendererpipeline.dataprovider.forest.ForestProvidable;
import com.recom.commons.model.maprendererpipeline.dataprovider.structure.StructureProvidable;
import com.recom.commons.model.maprendererpipeline.report.PipelineLogMessage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.MissingRequiredPropertiesException;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;


@Slf4j
@NoArgsConstructor
public class MapComposer {

    @NonNull
    private final List<MapLayerRasterizer> mapLayerRasterizerPipeline = new ArrayList<>();
    @NonNull
    private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
    @NonNull
    private final ARGBCalculator argbCalculator = new ARGBCalculator();
    @Getter
    @NonNull
    private Optional<ForestProvidable> forestProvider = Optional.empty();
    @Getter
    @NonNull
    private Optional<StructureProvidable> structureProvider = Optional.empty();


    public void registerForestProvider(@NonNull final ForestProvidable forestProvider) {
        this.forestProvider = Optional.of(forestProvider);
    }

    public void registerVillageProvider(@NonNull final StructureProvidable villageProvider) {
        this.structureProvider = Optional.of(villageProvider);
    }

    @NonNull
    public static MapComposer withDefaultConfiguration() {
        final MapComposer mapComposer = new MapComposer();

        //batch0
        mapComposer.registerRenderer(new SlopeAndAspectMapRasterizer());
        mapComposer.registerRenderer(new StructureClusterCreator(mapComposer));
        mapComposer.registerRenderer(new ForestClusterCreator(mapComposer));

        //batch1
        mapComposer.registerRenderer(new SlopeMapRasterizer());

        //batch2
        mapComposer.registerRenderer(new HeightMapRasterizer());
        mapComposer.registerRenderer(new LandMapRasterizer());
        mapComposer.registerRenderer(new BaseMapRasterizer());
        mapComposer.registerRenderer(new ShadowedMapRasterizer());
        mapComposer.registerRenderer(new StructureMapRasterizer(mapComposer));
        mapComposer.registerRenderer(new StructureClusterMapRasterizer(mapComposer));
        mapComposer.registerRenderer(new ForestMapRasterizer(mapComposer));
        mapComposer.registerRenderer(new ContourLineMapRasterizer());

        return mapComposer;
    }

    public void registerRenderer(@NonNull final MapLayerRasterizer rasterizer) {
        mapLayerRasterizerPipeline.add(rasterizer);
    }

    @NonNull
    public void execute(@NonNull final MapComposerWorkPackage workPackage) throws MissingRequiredPropertiesException {
        assertDataProvidersAreRegistered();
        applyConfigurationToRasterizer(workPackage);

        renderDataInParallelAndInBatches(workPackage);

        handleException(workPackage);
    }

    private void assertDataProvidersAreRegistered() {
        if (forestProvider.isEmpty() || structureProvider.isEmpty()) {
            log.error("No forest provider is registered!");
            throw new IllegalStateException("Data providers are not registered!");
        }
    }

    @NonNull
    private void renderDataInParallelAndInBatches(@NonNull final MapComposerWorkPackage workPackage) {
        mapLayerRasterizerPipeline.stream()
                .collect(Collectors.groupingBy(rasterizer -> rasterizer.getMapLayerRasterizerConfiguration().getBatch()))
                .entrySet().stream()
                .sorted(Comparator.comparingInt(Map.Entry::getKey))
                .forEach(batch -> {
                    final List<MapLayerRasterizer> taskListOfBatch = batch.getValue();
                    taskListOfBatch.stream()
                            .sorted(Comparator.comparingInt((final MapLayerRasterizer mapLayerRasterizer) -> mapLayerRasterizer.getMapLayerRasterizerConfiguration().getLayerOrder()))
                            .filter((final MapLayerRasterizer renderer) -> renderer.getMapLayerRasterizerConfiguration().isEnabled())
                            .map(renderer -> CompletableFuture.supplyAsync(() -> {
                                try {
                                    renderer.render(workPackage);
                                } catch (final Throwable t) {
                                    log.error("Failed to render data!", t);
                                    workPackage.getReport().logException(t);
                                }

                                return workPackage;
                            }, executorService))
                            .toList().stream() // terminate task creation before joining
                            .map(CompletableFuture::join)
                            .reduce(workPackage, (wrkPackage, b) -> wrkPackage, (wrkPackage, b) -> wrkPackage);
                });
    }

    private void handleException(@NonNull final MapComposerWorkPackage workPackage) throws MissingRequiredPropertiesException {
        if (!workPackage.getReport().isSuccess()) {
            workPackage.getReport().getMessages().stream()
                    .map(PipelineLogMessage::toString)
                    .forEach((log::error));

            throw new MissingRequiredPropertiesException();
        }
    }

    private void applyConfigurationToRasterizer(@NonNull final MapComposerWorkPackage workPackage) {
        final Map<String, MapLayerRasterizer> indexedRasterizer = mapLayerRasterizerPipeline.stream()
                .collect(HashMap::new, (map, rasterizer) -> map.put(rasterizer.getRasterizerName(), rasterizer), HashMap::putAll);

        workPackage.getMapComposerConfiguration().getRendererConfiguration().stream()
                .filter(configuration -> indexedRasterizer.containsKey(configuration.getRasterizerName()))
                .forEach(configuration -> {
                    final MapLayerRasterizer mapLayerRasterizer = indexedRasterizer.get(configuration.getRasterizerName());
                    configuration.applyTo(mapLayerRasterizer);
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
            @NonNull final int[] basePixelBuffer
    ) {
        return workPackage.getPipelineArtifacts().getArtifacts().entrySet().stream()
                .sorted(Comparator.comparingInt((entry) -> entry.getValue().getCreator().getMapLayerRasterizerConfiguration().getLayerOrder()))
                .filter(entry -> {
                    final MapLayerRasterizer artifactCreator = entry.getValue().getCreator();
                    return artifactCreator.getMapLayerRasterizerConfiguration().isEnabled()
                            && artifactCreator.getMapLayerRasterizerConfiguration().isVisible();
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
                .reduce(basePixelBuffer, (targetBuffer, artifactBuffer) -> {
                    for (int i = 0; i < targetBuffer.length; i++) {
                        targetBuffer[i] = argbCalculator.blend(artifactBuffer[i], targetBuffer[i]);
                    }

                    return targetBuffer;
                });
    }

}