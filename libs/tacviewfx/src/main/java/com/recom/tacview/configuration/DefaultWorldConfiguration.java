package com.recom.tacview.configuration;

import com.recom.tacview.engine.entitycomponentsystem.environment.Environment;
import com.recom.tacview.engine.module.DefaultEnvironment;
import com.recom.tacview.engine.renderer.RenderProvider;
import com.recom.tacview.property.RendererProperties;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;


@RequiredArgsConstructor
@AutoConfigureAfter(TacViewAutoConfiguration.class)
public class DefaultWorldConfiguration {

    @Bean
    @ConditionalOnMissingBean(Environment.class)
    public DefaultEnvironment defaultEngineModule(
            @NonNull final RendererProperties rendererProperties,
            @NonNull final RenderProvider renderProvider
    ) {
        return new DefaultEnvironment(rendererProperties, renderProvider);
    }

}
