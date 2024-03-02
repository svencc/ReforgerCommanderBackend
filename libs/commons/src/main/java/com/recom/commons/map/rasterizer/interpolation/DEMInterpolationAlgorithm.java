package com.recom.commons.map.rasterizer.interpolation;

import com.recom.commons.model.DEMDescriptor;
import lombok.NonNull;

public interface DEMInterpolationAlgorithm {

    float[][] interpolateDem(
            @NonNull final DEMDescriptor DEMDescriptor,
            final int scale
    );

    float[][] interpolateDem(
            @NonNull float[][] dem,
            final int scale
    );


}
