package com.recom.tacview.configuration;

import com.recom.tacview.engine.renderer.RenderProvider;
import com.recom.tacview.engine.module.EngineModuleTemplate;
import com.recom.tacview.engine.graphics.ScreenComposer;
import com.recom.tacview.engine.module.DefaultEngineModule;
import com.recom.tacview.property.RendererProperties;
import com.recom.tacview.service.RandomProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;


@RequiredArgsConstructor
@AutoConfigureAfter(TackViewAutoConfiguration.class)
public class DefaultEngineModuleConfiguration {

    @NonNull
    private final RendererProperties rendererProperties;
    @NonNull
    private final ScreenComposer screenComposer;
    @NonNull
    private final RandomProvider randomProvider;
    @NonNull
    private final RenderProvider renderProvider;

    @Bean
    @ConditionalOnMissingBean(EngineModuleTemplate.class)
    public DefaultEngineModule defaultEngineModule(@NonNull final RendererProperties rendererProperties) {
        return new DefaultEngineModule(rendererProperties, screenComposer, randomProvider, renderProvider);
    }

}
