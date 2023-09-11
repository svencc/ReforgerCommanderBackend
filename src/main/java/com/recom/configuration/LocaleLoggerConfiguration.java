package com.recom.configuration;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;

import java.util.Locale;
import java.util.TimeZone;

@Slf4j
@Configuration
public class LocaleLoggerConfiguration implements AsyncConfigurer {

    @PostConstruct
    public void init() {
        log.info("Locale initialized with '{}'", Locale.getDefault());
    }

}