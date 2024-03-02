package com.recom.commons.map.rasterizer.interpolation;

import com.recom.commons.model.DEMDescriptor;
import lombok.NonNull;

public interface DEMInterpolationAlgorithm {

    int[] scaleMap(
            @NonNull final DEMDescriptor DEMDescriptor,
            final int scale,
            @NonNull final int[] originalDEM
    );

}
