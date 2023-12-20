package com.recom.commander.factory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.support.DefaultFormattingConversionService;

@Configuration
public class ConversionServiceFactory {

    @Bean
    public ConversionService conversionService() {
        return new DefaultFormattingConversionService();
    }

}
