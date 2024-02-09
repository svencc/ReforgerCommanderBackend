package com.recom.tacview.configuration;

import com.recom.tacview.engine.ecs.environment.Environment;
import com.recom.tacview.engine.module.DefaultEnvironment;
import com.recom.tacview.engine.renderer.RenderProvider;
import com.recom.tacview.property.EngineProperties;
import com.recom.tacview.service.RendererExecutorProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;


@RequiredArgsConstructor
@AutoConfigureAfter(TacViewAutoConfiguration.class)
public class DefaultEnvironmentConfiguration {

    @NonNull
    private final EngineProperties engineProperties;
    @NonNull
    private final RenderProvider renderProvider;
    @NonNull
    private final RendererExecutorProvider rendererExecutorProvider;


    @Bean
    @ConditionalOnMissingBean(Environment.class)
    public DefaultEnvironment defaultEnvironment() {
        return new DefaultEnvironment(engineProperties, renderProvider, rendererExecutorProvider);
    }

}
