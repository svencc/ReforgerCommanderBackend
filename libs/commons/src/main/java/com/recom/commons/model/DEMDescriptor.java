package com.recom.commons.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DEMDescriptor implements Cloneable {

    // meta data
    private Float stepSize;
    private Integer scanIterationsX;
    private Integer scanIterationsZ;

    // surface data
    private Float seaLevel;
    private Float maxWaterDepth;
    private Float maxHeight;
    private float[][] dem;

    public int getDemWidth() {
        return dem.length;
    }

    public int getDemHeight() {
        return dem[0].length;
    }


    @Override
    public DEMDescriptor clone() {
        return DEMDescriptor.builder()
                .stepSize(stepSize)
                .scanIterationsX(scanIterationsX)
                .scanIterationsZ(scanIterationsZ)
                .seaLevel(seaLevel)
                .maxWaterDepth(maxWaterDepth)
                .maxHeight(maxHeight)
                .dem(dem)
                .build();
    }

}
