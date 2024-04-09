package com.recom.commons.model;

import com.recom.commons.math.Round;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DEMDescriptor implements Cloneable {

    // meta data
    private BigDecimal stepSize;
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
        return Round.halfUp(stepSize.multiply(BigDecimal.valueOf(getDemWidth())).intValue());
    }

    public int getMapHeightInMeter() {
        return Round.halfUp(stepSize.multiply(BigDecimal.valueOf(getDemHeight())).intValue());
    }

//    public Integer getStepSize() {
//        return stepSize.intValue();
//    }

    public BigDecimal getStepSize() {
        return stepSize;
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
