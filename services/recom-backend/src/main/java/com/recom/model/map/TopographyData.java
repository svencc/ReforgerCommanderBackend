package com.recom.model.map;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class TopographyData implements Serializable {

    // META DATA
    private final Integer stepSize;

    private final Integer scanIterationsX;

    private final Integer scanIterationsZ;

    private final Float oceanBaseHeight;


    // SURFACE DATA
    private final float[][] surfaceData;

}
