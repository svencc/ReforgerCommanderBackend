package com.recom.commander.factory;

import com.recom.commander.exception.exceptions.ApplicationStartupException;
import com.recom.commander.property.user.DynamicAuthenticationProperties;
import com.recom.commander.property.user.DynamicEngineProperties;
import com.recom.commander.property.user.DynamicHostProperties;
import com.recom.commander.service.property.RECOMPropertyBinderService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Slf4j
@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
public class PropertiesFactory {

    @NonNull
    private final RECOMPropertyBinderService RECOMPropertyBinderService;


    @Bean
    @SneakyThrows(ApplicationStartupException.class)
    public DynamicHostProperties createDynamicHostProperties() {
        return RECOMPropertyBinderService.bind(new DynamicHostProperties());
    }

    @Bean
    @SneakyThrows(ApplicationStartupException.class)
    public DynamicAuthenticationProperties createDynamicAuthenticationProperties() {
        return RECOMPropertyBinderService.bind(new DynamicAuthenticationProperties());
    }

    @Bean
    @SneakyThrows(ApplicationStartupException.class)
    public DynamicEngineProperties createDynamicEngineProperties() {
        return RECOMPropertyBinderService.bind(new DynamicEngineProperties());
    }

}
