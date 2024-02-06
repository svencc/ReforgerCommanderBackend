package com.recom.configuration;

import com.recom.commons.calculator.ARGBCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
public class ColorCalculatorConfiguration {

    @Bean()
    public ARGBCalculator getARGBCalculator() {
        return new ARGBCalculator();
    }

}