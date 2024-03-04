package com.recom.configuration;

import com.recom.commons.map.MapComposer;
import com.recom.commons.map.rasterizer.interpolation.DEMInterpolationAlgorithm;
import com.recom.commons.map.rasterizer.interpolation.DEMInterpolationAlgorithmBilinear;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
public class MapComposerConfiguration {


    @Bean()
    public MapComposer mapComposer() {
        return MapComposer.withDefaultConfiguration();
    }


    @Bean()
    public DEMInterpolationAlgorithm demInterpolationAlgorithm() {
        return new DEMInterpolationAlgorithmBilinear();
    }

}