package com.recom.commons.map.rasterizer.interpolation;

import com.recom.commons.model.DEMDescriptor;
import lombok.NonNull;

public interface DEMInterpolationAlgorithm {

    float[][] interpolate(
            @NonNull final DEMDescriptor DEMDescriptor,
            final int scale
    );

    float[][] interpolate(
            @NonNull float[][] dem,
            final int scale
    );


}
