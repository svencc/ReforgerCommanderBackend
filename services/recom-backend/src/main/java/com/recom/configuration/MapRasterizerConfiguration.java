package com.recom.configuration;

import com.recom.commons.map.MapComposer;
import com.recom.commons.map.rasterizer.ContourMapRasterizer;
import com.recom.commons.map.rasterizer.HeightMapRasterizer;
import com.recom.commons.map.rasterizer.ShadowedMapRasterizer;
import com.recom.commons.map.rasterizer.SlopeMapRasterizer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
public class MapRasterizerConfiguration {

    @Bean()
    public HeightMapRasterizer heightmapRasterizer() {
        return new HeightMapRasterizer();
    }

    @Bean()
    public ShadowedMapRasterizer shadowedMapRasterizer() {
        return new ShadowedMapRasterizer();
    }

    @Bean()
    public ContourMapRasterizer contourMapRasterizer() {
        return new ContourMapRasterizer();
    }

    @Bean()
    public SlopeMapRasterizer slopeMapRasterizer() {
        return new SlopeMapRasterizer();
    }

    @Bean()
    public MapComposer mapComposer() {
        return MapComposer.withDefaultConfiguration();
    }

}