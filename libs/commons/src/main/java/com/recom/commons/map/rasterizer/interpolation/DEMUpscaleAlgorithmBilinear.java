package com.recom.commons.map.rasterizer.interpolation;

import com.recom.commons.model.DEMDescriptor;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

@NoArgsConstructor
public class DEMUpscaleAlgorithmBilinear {

    @NonNull
    private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();


    public float[][] scaleUp(
            @NonNull DEMDescriptor demDescriptor,
            final int scaleFactor
    ) {
        return scaleUp(demDescriptor.getDem(), scaleFactor);
    }

    public float[][] scaleUp(
            @NonNull float[][] dem,
            final int scaleFactor
    ) {
        return linearInterpolation(dem, scaleFactor);
    }

    @NonNull
    private float[][] linearInterpolation(
            @NonNull final float[][] input,
            final int scaleFactor
    ) {
        final int originalHeight = input.length;
        final int originalWidth = input[0].length;
        final int newHeight = originalHeight * scaleFactor;
        final int newWidth = originalWidth * scaleFactor;
        final float[][] output = new float[newHeight][newWidth];

        IntStream.range(0, newHeight).parallel()
                .mapToObj(y -> CompletableFuture.supplyAsync(() -> {
                    for (int x = 0; x < newWidth; x++) {
                        final float gy = ((float) y) / (newHeight - 1) * (originalHeight - 1);
                        final float gx = ((float) x) / (newWidth - 1) * (originalWidth - 1);
                        final int gyi = (int) gy;
                        final int gxi = (int) gx;

                        final float c11 = input[gyi][gxi];
                        final float c21 = gyi + 1 < originalHeight ? input[gyi + 1][gxi] : c11;
                        final float c12 = gxi + 1 < originalWidth ? input[gyi][gxi + 1] : c11;
                        final float c22 = (gyi + 1 < originalHeight && gxi + 1 < originalWidth) ? input[gyi + 1][gxi + 1] : c11;

                        output[y][x] = interpolate(c11, c21, c12, c22, gyi, gyi + 1, gxi, gxi + 1, gy, gx);
                    }

                    return Boolean.TRUE;
                }, executorService))
                .forEach(CompletableFuture::join);

        return output;
    }

    private float interpolate(
            final float q11, final float q21,
            final float q12, final float q22,
            final float y1, final float y2,
            final float x1, final float x2,
            final float y, final float x
    ) {
        final float x2x1 = y2 - y1;
        final float y2y1 = x2 - x1;
        final float x2x = y2 - y;
        final float y2y = x2 - x;
        final float yy1 = x - x1;
        final float xx1 = y - y1;

        return 1.0f / (x2x1 * y2y1) * (
                q11 * x2x * y2y +
                        q21 * xx1 * y2y +
                        q12 * x2x * yy1 +
                        q22 * xx1 * yy1
        );
    }

}