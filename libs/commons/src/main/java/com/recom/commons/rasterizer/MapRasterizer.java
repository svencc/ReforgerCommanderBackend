package com.recom.commons.rasterizer;

import com.recom.commons.model.MapRendererPipelineArtefacts;
import com.recom.commons.rasterizer.meta.MapLayerPipelineRenderer;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;





// @TODO -> das ganze geht dann vermutlich in Backend oder Commander und ist dort Konfigurierbar?
@Slf4j
public class MapRasterizer {

    @NonNull
    private final List<MapLayerPipelineRenderer> mapLayerPipelineRenderers = new ArrayList<>();


    public MapRasterizer() {
        mapLayerPipelineRenderers.add(new ContourMapRasterizer());
        mapLayerPipelineRenderers.add(new SlopeAndAspectMapRasterizer());
        mapLayerPipelineRenderers.add(new SlopeMapRasterizer());
        mapLayerPipelineRenderers.add(new ShadowedMapRasterizer());
    }

    @NonNull
    public ByteArrayOutputStream renderMap(@NonNull final MapRendererPipelineArtefacts pipelineArtefacts) throws IOException {
        // @TODO: execute, after core results, the renderers in parallel!
        // use CompletableFuture.runAsync(() -> renderer.render(pipelineArtefacts), executorService); -> virtual threads!
        // Execute all renderers
        mapLayerPipelineRenderers.stream()
                .sorted(Comparator.comparing(MapLayerPipelineRenderer::getLayerOrder))
                .forEach(renderer -> {
                    try {
                        renderer.render(pipelineArtefacts);
                    } catch (final IOException e) {
                        pipelineArtefacts.getOccurredExceptionsDuringPipelineExecution().put(renderer.getClass(), e);
                    }
                });

        // Error logging and "rethrow" one exception
        // @TODO: use a custom exception to wrap all exceptions and throw it!
        if (!pipelineArtefacts.getOccurredExceptionsDuringPipelineExecution().isEmpty()) {
            pipelineArtefacts.getOccurredExceptionsDuringPipelineExecution()
                    .forEach((key, value) -> log.error("An error occurred during map rendering {}:", key.getSimpleName(), value));
            throw new IOException("One or more errors occurred during map rendering");
        }


        // @TODO
        // Problem ist jetzt dass ich die buffer; nicht die image ByteStreams brauche; plus in der richtigen reihenfolge!
        // diese infos sind ja im renderer drin, brauche das in der Ergebnissliste auch -> übertragung von renderer zu pipelineArtefacts


        // -------------------------------------------------------------------------------------------------------------
        // prepare pixel buffer
        final int width = pipelineArtefacts.getDemDescriptor().getDemWidth();
        final int height = pipelineArtefacts.getDemDescriptor().getDemHeight();
        final int[] pixelBuffer = new int[width * height];
        // ---


        // write buffered pixels to image
        final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        final int[] imagePixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(pixelBuffer, 0, imagePixels, 0, pixelBuffer.length);

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", outputStream);
        // ---

        return outputStream;
    }

}