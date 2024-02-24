package com.recom.commons.rasterizer.meta;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum LayerOrder {
    HEIGHT_MAP(100),
    SLOPE_AND_ASPECT_MAP(200),
    SHADOWED_MAP(300),
    CONTOUR_MAP(400),
    SLOPE_MAP(500);

    private final int order;

}
