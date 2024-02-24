package com.recom.commander.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.support.DefaultFormattingConversionService;

@Configuration(proxyBeanMethods = false)
public class ConversionServiceConfiguration {

    @Bean
    public ConversionService conversionService() {
        return new DefaultFormattingConversionService();
    }

}
