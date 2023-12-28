package com.recom.tacview.configuration;

import com.recom.tacview.engine.entity.World;
import com.recom.tacview.engine.module.DefaultWorld;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;


@RequiredArgsConstructor
@AutoConfigureAfter(TacViewAutoConfiguration.class)
public class DefaultWorldConfiguration {

    @Bean
    @ConditionalOnMissingBean(World.class)
    public DefaultWorld defaultEngineModule() {
        return new DefaultWorld();
    }

}
