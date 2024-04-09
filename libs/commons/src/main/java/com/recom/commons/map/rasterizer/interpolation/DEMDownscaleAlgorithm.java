package com.recom.commons.map.rasterizer.interpolation;

import com.recom.commons.model.DEMDescriptor;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor
public class DEMDownscaleAlgorithm {

    @NonNull
    public float[][] scaleDown(
            @NonNull DEMDescriptor demDescriptor,
            final int pickNth
    ) {
        return scaleDown(demDescriptor.getDem(), pickNth);
    }

    @NonNull
    public float[][] scaleDown(
            @NonNull float[][] dem,
            final int pickNth
    ) {
        // @TODO implement this method ! -> test!
        return subsample2DArray(dem, pickNth);
    }

    @NonNull
    private float[][] subsample2DArray(
            @NonNull final float[][] input,
            final int pickNth
    ) {
        final int originalWidth = input.length;
        final int originalHeight = input[0].length;
        final int newWidth = (originalWidth + pickNth - 1) / pickNth;
        final int newHeight = (originalHeight + pickNth - 1) / pickNth;
        final float[][] output = new float[newWidth][newHeight];

        for (int y = 0, newY = 0; y < originalWidth; y += pickNth, newY++) {
            for (int x = 0, newX = 0; x < originalHeight; x += pickNth, newX++) {
                output[newY][newX] = input[y][x];
            }
        }

        return output;
    }

}