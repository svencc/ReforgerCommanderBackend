package com.recom.commander.configuration;

import com.recom.commons.rasterizer.HeightMapRasterizer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
public class HeightmapRasterizerConfiguration {

    @Bean()
    public HeightMapRasterizer getHeightmapRasterizer() {
        return new HeightMapRasterizer();
    }

}