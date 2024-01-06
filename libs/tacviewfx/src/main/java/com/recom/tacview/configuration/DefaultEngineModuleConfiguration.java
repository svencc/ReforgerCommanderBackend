package com.recom.tacview.configuration;

import com.recom.tacview.engine.entitycomponentsystem.environment.Environment;
import com.recom.tacview.engine.module.DefaultEngineModule;
import com.recom.tacview.engine.module.EngineModule;
import com.recom.tacview.engine.renderer.RenderProvider;
import com.recom.tacview.property.RendererProperties;
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
    private final RenderProvider renderProvider;

    @Bean
    @ConditionalOnMissingBean(EngineModule.class)
    public DefaultEngineModule defaultEngineModule(@NonNull final RendererProperties rendererProperties) {
        return new DefaultEngineModule(environment, renderProvider);
    }

}
