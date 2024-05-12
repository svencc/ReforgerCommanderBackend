package com.recom.commons.map.rasterizer.interpolation;

import com.recom.commons.model.DEMDescriptor;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
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
        final int originalHeight_ = input.length;
        final int originalWidth = input[0].length;
        final int newHeight_ = (int) (originalHeight_ * scaleFactor);
        final int newWidth_ = (int) (originalWidth * scaleFactor);
        final float[][] output = new float[newHeight_][newWidth_];


        IntStream.range(0, newHeight_).parallel()
                .mapToObj(y_ -> CompletableFuture.supplyAsync(() -> {
                    for (int x_ = 0; x_ < newWidth_; x_++) {
                        final float gy_ = ((float) y_) / (newHeight_ - 1) * (originalHeight_ - 1);
                        final float gx_ = ((float) x_) / (newWidth_ - 1) * (originalWidth - 1);
                        final int gyi_ = (int) gy_;
                        final int gxi_ = (int) gx_;

                        final float c11 = input[gyi_][gxi_];
                        final float c21 = gyi_ + 1 < originalHeight_ ? input[gyi_ + 1][gxi_] : c11;
                        final float c12 = gxi_ + 1 < originalWidth ? input[gyi_][gxi_ + 1] : c11;
                        final float c22 = (gyi_ + 1 < originalHeight_ && gxi_ + 1 < originalWidth) ? input[gyi_ + 1][gxi_ + 1] : c11;

                        output[y_][x_] = interpolate(c11, c21, c12, c22, gyi_, gyi_ + 1, gxi_, gxi_ + 1, gy_, gx_);
                    }

                    return Boolean.TRUE;
                }, executorService))
                .toList().stream() // terminate task creation before joining
                .map(CompletableFuture::join)
                .collect(Collectors.toList());


        /*
        IntStream.range(0, newHeight_).parallel().forEach(y_ -> {
            for (int x_ = 0; x_ < newWidth_; x_++) {
                final float gy_ = ((float) y_) / (newHeight_ - 1) * (originalHeight_ - 1);
                final float gx_ = ((float) x_) / (newWidth_ - 1) * (originalWidth - 1);
                final int gyi_ = (int) gy_;
                final int gxi_ = (int) gx_;

                final float c11 = input[gyi_][gxi_];
                final float c21 = gyi_ + 1 < originalHeight_ ? input[gyi_ + 1][gxi_] : c11;
                final float c12 = gxi_ + 1 < originalWidth ? input[gyi_][gxi_ + 1] : c11;
                final float c22 = (gyi_ + 1 < originalHeight_ && gxi_ + 1 < originalWidth) ? input[gyi_ + 1][gxi_ + 1] : c11;

                output[y_][x_] = interpolate(c11, c21, c12, c22, gyi_, gyi_ + 1, gxi_, gxi_ + 1, gy_, gx_);
            }
        });
         */


//        for (int y_ = 0; y_ < newHeight_; y_++) {
//            for (int x_ = 0; x_ < newWidth_; x_++) {
//                final float gy_ = ((float) y_) / (newHeight_ - 1) * (originalHeight_ - 1);
//                final float gx_ = ((float) x_) / (newWidth_ - 1) * (originalWidth - 1);
//                final int gyi_ = (int) gy_;
//                final int gxi_ = (int) gx_;
//
//                final float c11 = input[gyi_][gxi_];
//                final float c21 = gyi_ + 1 < originalHeight_ ? input[gyi_ + 1][gxi_] : c11;
//                final float c12 = gxi_ + 1 < originalWidth ? input[gyi_][gxi_ + 1] : c11;
//                final float c22 = (gyi_ + 1 < originalHeight_ && gxi_ + 1 < originalWidth) ? input[gyi_ + 1][gxi_ + 1] : c11;
//
//                output[y_][x_] = interpolate(c11, c21, c12, c22, gyi_, gyi_ + 1, gxi_, gxi_ + 1, gy_, gx_);
//            }
//        }

        return output;
    }

    private float interpolate(
            final float q11, final float q21,
            final float q12, final float q22,
            final float y1_, final float y2_,
            final float x1_, final float x2_,
            final float y_, final float x_
    ) {
        final float x2x1 = y2_ - y1_;
        final float y2y1 = x2_ - x1_;
        final float x2x = y2_ - y_;
        final float y2y = x2_ - x_;
        final float yy1 = x_ - x1_;
        final float xx1 = y_ - y1_;

        return 1.0f / (x2x1 * y2y1) * (
                q11 * x2x * y2y +
                        q21 * xx1 * y2y +
                        q12 * x2x * yy1 +
                        q22 * xx1 * yy1
        );
    }

}