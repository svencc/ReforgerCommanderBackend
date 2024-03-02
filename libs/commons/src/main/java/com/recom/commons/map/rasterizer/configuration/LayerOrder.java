package com.recom.commons.map.rasterizer.configuration;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LayerOrder {

    public static int SLOPE_AND_ASPECT_MAP = 100;
    public static int SLOPE_MAP = 110;
    public static int BASE_MAP = 200;
    public static int HEIGHT_MAP = 300;
    public static int SHADOWED_MAP = 400;
    public static int CONTOUR_MAP = 500;

}
