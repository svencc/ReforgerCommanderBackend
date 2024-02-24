package com.recom.configuration;

import com.recom.commons.rasterizer.MapRasterizer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
public class HeightmapRasterizerConfiguration {

    @Bean()
    public MapRasterizer getHeightmapRasterizer() {
        return new MapRasterizer();
    }

}