package com.recom.commons.model;

import com.recom.commons.math.Round;
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
    private Integer stepSize;
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

    public int getMapWidthInMeter() {
        return Round.halfUp(getDemWidth() * stepSize);
    }

    public int getMapHeightInMeter() {
        return Round.halfUp(getDemHeight() * stepSize);
    }

    public Integer getStepSize() {
        return stepSize.intValue();
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
