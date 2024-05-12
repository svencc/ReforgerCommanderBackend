package com.recom.commons.map.rasterizer.interpolation;

import com.recom.commons.model.DEMDescriptor;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor
public class DEMUpscaleAlgorithmBilinearBAK {

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
        final int originalWidth = input.length;
        final int originalHeight = input[0].length;
        final int newHeight = (int) (originalHeight * scaleFactor);
        final int newWidth = (int) (originalWidth * scaleFactor);
        final float[][] output = new float[newHeight][newWidth];

        for (int coordinateY = 0; coordinateY < newHeight; coordinateY++) {
            for (int coordinateX = 0; coordinateX < newWidth; coordinateX++) {
                final float gx = ((float) coordinateX) / (newWidth - 1) * (originalWidth - 1);
                final float gy = ((float) coordinateY) / (newHeight - 1) * (originalHeight - 1);
                final int gyi = (int) gy;
                final int gxi = (int) gx;

                final float c11 = input[gyi][gxi];
                final float c21 = gyi + 1 < originalHeight ? input[gyi + 1][gxi] : c11;
                final float c12 = gxi + 1 < originalWidth ? input[gyi][gxi + 1] : c11;
                final float c22 = (gyi + 1 < originalHeight && gxi + 1 < originalWidth) ? input[gyi + 1][gxi + 1] : c11;

                output[coordinateY][coordinateX] = interpolate(c11, c21, c12, c22, gyi, gyi + 1, gxi, gxi + 1, gx, gy);
            }
        }

        return output;
    }

    private float interpolate(
            final float q11, final float q21,
            final float q12, final float q22,
            final float _y1, final float _y2,
            final float _x1, final float _x2,
            final float x, final float y
    ) {
        final float x2x1 = _y2 - _y1;
        final float y2y1 = _x2 - _x1;
        final float x2x = _y2 - x;
        final float y2y = _x2 - y;
        final float yy1 = y - _x1;
        final float xx1 = x - _y1;

        return 1.0f / (x2x1 * y2y1) * (
                q11 * x2x * y2y +
                        q21 * xx1 * y2y +
                        q12 * x2x * yy1 +
                        q22 * xx1 * yy1
        );
    }

}