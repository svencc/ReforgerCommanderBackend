package com.recom.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateHeightMapCommand {

    private Float seaLevel;

    private Float maxWaterDepth;

    private Float maxHeight;

    private float[][] heightMap;

}
