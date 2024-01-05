package com.recom.tacview.configuration;

import com.recom.tacview.engine.entity.Environment;
import com.recom.tacview.engine.module.DefaultEnvironment;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;


@RequiredArgsConstructor
@AutoConfigureAfter(TacViewAutoConfiguration.class)
public class DefaultWorldConfiguration {

    @Bean
    @ConditionalOnMissingBean(Environment.class)
    public DefaultEnvironment defaultEngineModule() {
        return new DefaultEnvironment();
    }

}
