package com.recom.commander.configuration;

import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.support.DefaultFormattingConversionService;

@Configuration
public class ConversionServiceFactory {

    @Bean
    public ConversionService conversionService() {
        return new DefaultFormattingConversionService();
//        DefaultFormattingConversionServiceFactoryBean bean = new DefaultFormattingConversionServiceF();
//        ConversionServiceFactoryBean bean = new ConversionServiceFactoryBean();
////        bean.setConverters(...); //add converters
//        bean.afterPropertiesSet();
//
//        return bean.getObject();
    }

}
