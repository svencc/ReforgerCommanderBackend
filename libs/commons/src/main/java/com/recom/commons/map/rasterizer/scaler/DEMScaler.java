package com.recom.commons.map.rasterizer.scaler;

import com.recom.commons.model.DEMDescriptor;
import lombok.NonNull;

public interface DEMScaler {

    int[] scaleMap(
            @NonNull final DEMDescriptor DEMDescriptor,
            final int scale,
            @NonNull final int[] originalDEM
    );

}
