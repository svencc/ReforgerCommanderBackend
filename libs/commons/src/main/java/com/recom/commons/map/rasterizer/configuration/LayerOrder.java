package com.recom.commons.map.rasterizer.configuration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum LayerOrder {
    SLOPE_AND_ASPECT_MAP(100),
    BASE_MAP(200),
    HEIGHT_MAP(300),
    SHADOWED_MAP(400),
    CONTOUR_MAP(500),
    SLOPE_MAP(600);

    @Getter
    private final int order;

}
