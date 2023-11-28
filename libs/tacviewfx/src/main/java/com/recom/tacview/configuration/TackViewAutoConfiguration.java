package com.recom.tacview.configuration;

import com.recom.tacview.property.ComposerProperties;
import com.recom.tacview.property.RendererProperties;
import com.recom.tacview.property.TickProperties;
import lombok.NonNull;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;


@AutoConfiguration
public class TackViewAutoConfiguration {

    @Bean
    public ComposerProperties composerProperties() {
        return ComposerProperties.builder()
                .parallelizedBackBufferHandler(true)
                .backBufferSize(6)
                .build();
    }

    @Bean
    public RendererProperties rendererProperties(@NonNull final ComposerProperties composerProperties) {
        return RendererProperties.builder()
                .width(1920)
                .height(1080)
                .parallelizedRendering(true)
                .threadPoolSize(4)
                .composer(composerProperties)
                .build();
    }

    @Bean
    public TickProperties tickProperties() {
        return TickProperties.builder()
                .ticksPerSecond(60)
                .build();
    }

}
