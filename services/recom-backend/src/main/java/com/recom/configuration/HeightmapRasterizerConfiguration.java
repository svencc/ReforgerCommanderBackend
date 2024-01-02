package com.recom.configuration;

import com.recom.rendertools.rasterizer.HeightmapRasterizer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
public class HeightmapRasterizerConfiguration {

    @Bean()
    public HeightmapRasterizer getHeightmapRasterizer() {
        return new HeightmapRasterizer();
    }

}