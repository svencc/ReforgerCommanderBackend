package com.recom.commons.map.rasterizer.interpolation;

import com.recom.commons.model.DEMDescriptor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DEMInterpolationAlgorithmLinear implements DEMInterpolationAlgorithm {

    @Override
    public float[][] interpolateDem(
            @NonNull DEMDescriptor DEMDescriptor,
            final int scaleFactor
    ) {
        return interpolateDem(DEMDescriptor.getDem(), scaleFactor);
    }

    @Override
    public float[][] interpolateDem(
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
        final int originalWidth = input.length;
        final int originalHeight = input[0].length;
        final int newWidth = (int) (originalWidth * scaleFactor);
        final int newHeight = (int) (originalHeight * scaleFactor);
        final float[][] output = new float[newWidth][newHeight];

        for (int y = 0; y < newHeight; y++) {
            for (int x = 0; x < newWidth; x++) {
                final float gx = ((float) x) / (newWidth - 1) * (originalWidth - 1);
                final float gy = ((float) y) / (newHeight - 1) * (originalHeight - 1);
                final int gxi = (int) gx;
                final int gyi = (int) gy;

                final float c11 = input[gxi][gyi];
                final float c21 = gxi + 1 < originalWidth ? input[gxi + 1][gyi] : c11;
                final float c12 = gyi + 1 < originalHeight ? input[gxi][gyi + 1] : c11;
                final float c22 = (gxi + 1 < originalWidth && gyi + 1 < originalHeight) ? input[gxi + 1][gyi + 1] : c11;

                output[x][y] = interpolate(c11, c21, c12, c22, gxi, gxi + 1, gyi, gyi + 1, gx, gy);
            }
        }

        return output;
    }

    private float interpolate(
            final float q11, final float q21,
            final float q12, final float q22,
            final float x1, final float x2,
            final float y1, final float y2,
            final float x, final float y
    ) {
        final float x2x1 = x2 - x1;
        final float y2y1 = y2 - y1;
        final float x2x = x2 - x;
        final float y2y = y2 - y;
        final float yy1 = y - y1;
        final float xx1 = x - x1;

        return 1.0f / (x2x1 * y2y1) * (
                q11 * x2x * y2y +
                        q21 * xx1 * y2y +
                        q12 * x2x * yy1 +
                        q22 * xx1 * yy1
        );
    }

}