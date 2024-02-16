package com.recom.tacview.configuration;

import com.recom.tacview.property.EngineProperties;
import com.recom.tacview.property.IsEngineProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;


@AutoConfiguration
@RequiredArgsConstructor
@ComponentScan("com.recom.tacview")
public class TacViewAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(IsEngineProperties.class)
    public IsEngineProperties createDefaultEngineProperties() {
        return EngineProperties.builder()
                .rendererWidth(640)
                .rendererHeight(480)
                .rendererScale(1)
                .parallelizedRendering(true)
                .renderFragments(1)
                .rendererThreadPoolSize(-1)
                .targetFps(60)
                .composerBackBufferSize(10)
                .targetTps(60)
                .build();
    }

}
