package com.recom.service.map.topography;

import com.recom.rendertools.rasterizer.HeightmapRasterizer;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
class HeightmapRasterizerProvider {

    @NonNull
    private final HeightmapRasterizer heightmapRasterizer;

    public HeightmapRasterizerProvider() {
        this.heightmapRasterizer = new HeightmapRasterizer();
    }


    public HeightmapRasterizer provide() {
        return heightmapRasterizer;
    }

}