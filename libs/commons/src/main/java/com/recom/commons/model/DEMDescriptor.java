package com.recom.commons.model;

import com.recom.commons.math.Round;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DEMDescriptor implements Cloneable {

    // meta data
    @Getter
    private BigDecimal stepSize;

    // surface data
    private Float seaLevel;
    private Float maxWaterDepth;
    private Float maxHeight;
    private float[][] dem;

    public int getDemHeight() {
        return dem.length;
    }

    public int getDemWidth() {
        return dem[0].length;
    }

    public int getMapWidthInMeter() {
        return Round.halfUp(stepSize.multiply(BigDecimal.valueOf(getDemWidth())).intValue());
    }

    public int getMapHeightInMeter() {
        return Round.halfUp(stepSize.multiply(BigDecimal.valueOf(getDemHeight())).intValue());
    }

    @Override
    public DEMDescriptor clone() {
        return DEMDescriptor.builder()
                .stepSize(stepSize)
                .seaLevel(seaLevel)
                .maxWaterDepth(maxWaterDepth)
                .maxHeight(maxHeight)
                .dem(dem)
                .build();
    }

}
