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

    private float seaLevel;

    private float maxWaterDepth;

    private float maxHeight;

    private float[][] heightMap;

}
