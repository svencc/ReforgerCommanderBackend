package com.recom.commons.map.rasterizer.configuration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum LayerOrder {
    SLOPE_AND_ASPECT_MAP(100),
    HEIGHT_MAP(200),
    SHADOWED_MAP(300),
    CONTOUR_MAP(400),
    SLOPE_MAP(500);

    @Getter
    private final int order;

}
