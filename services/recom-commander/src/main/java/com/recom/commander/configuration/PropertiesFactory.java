package com.recom.commander.configuration;

import com.recom.commander.property.user.HostProperties;
import com.recom.commander.service.property.RECOMPropertyBinderService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Slf4j
@Configuration
@RequiredArgsConstructor
public class PropertiesFactory {

    @NonNull
    private final RECOMPropertyBinderService RECOMPropertyBinderService;

    @Bean
    public HostProperties hostProperties() {
        return RECOMPropertyBinderService.bind(new HostProperties());
    }

}
