package com.recom.configuration;

import com.recom.commons.map.MapComposer;
import com.recom.commons.map.rasterizer.interpolation.DEMDownscaleAlgorithm;
import com.recom.commons.map.rasterizer.interpolation.DEMUpscaleAlgorithmBilinear;
import com.recom.commons.map.rasterizer.interpolation.PixelScaler;
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
    public DEMUpscaleAlgorithmBilinear demUpscaleAlgorithmBilinear() {
        return new DEMUpscaleAlgorithmBilinear();
    }

    @Bean()
    public PixelScaler pixelScaler() {
        return new PixelScaler();
    }

    @Bean()
    public DEMDownscaleAlgorithm demDownscaleAlgorithm() {
        return new DEMDownscaleAlgorithm();
    }

}