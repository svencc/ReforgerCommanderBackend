package com.recom.tacview.configuration;

import com.recom.tacview.property.ComposerProperties;
import com.recom.tacview.property.RendererProperties;
import com.recom.tacview.property.TickProperties;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;


@AutoConfiguration
@RequiredArgsConstructor
@ComponentScan("com.recom.tacview.module")
public class TackViewAutoConfiguration {

    @Bean
    public RendererProperties rendererProperties(@NonNull final ComposerProperties composerProperties) {
        return RendererProperties.builder()
                .width(640)
                .height(480)
                .parallelizedRendering(true)
                .threadPoolSize(8)
                .composer(composerProperties)
                .build();
    }

    @Bean
    public ComposerProperties composerProperties() {
        return ComposerProperties.builder()
                .backBufferSize(6)
                .build();
    }

    @Bean
    public TickProperties tickProperties() {
        return TickProperties.builder()
                .ticksPerSecond(60)
                .build();
    }

}
