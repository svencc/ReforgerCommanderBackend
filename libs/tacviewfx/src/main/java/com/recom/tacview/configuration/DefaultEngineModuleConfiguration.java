package com.recom.tacview.configuration;

import com.recom.tacview.engine.ecs.environment.Environment;
import com.recom.tacview.engine.module.DefaultEngineModule;
import com.recom.tacview.engine.module.EngineModule;
import com.recom.tacview.service.RandomProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;


@RequiredArgsConstructor
@AutoConfigureAfter(DefaultEnvironmentConfiguration.class)
public class DefaultEngineModuleConfiguration {

    @NonNull
    private final Environment environment;
    @NonNull
    final RandomProvider randomProvider;

    @Bean
    @ConditionalOnMissingBean(EngineModule.class)
    public DefaultEngineModule defaultEngineModule() {
        return new DefaultEngineModule(environment, randomProvider);
    }

}
