package com.recom.commander.factory;

import com.recom.commander.exception.exceptions.ApplicationStartupException;
import com.recom.commander.property.user.AuthenticationProperties;
import com.recom.commander.property.user.EngineProperties;
import com.recom.commander.property.user.HostProperties;
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
    public HostProperties createHostProperties() {
        return RECOMPropertyBinderService.bind(new HostProperties());
    }

    @Bean
    @SneakyThrows(ApplicationStartupException.class)
    public AuthenticationProperties createAuthenticationProperties() {
        return RECOMPropertyBinderService.bind(new AuthenticationProperties());
    }

    @Bean
    @SneakyThrows(ApplicationStartupException.class)
    public EngineProperties createEngineProperties() {
        return RECOMPropertyBinderService.bind(new EngineProperties());
    }

}
