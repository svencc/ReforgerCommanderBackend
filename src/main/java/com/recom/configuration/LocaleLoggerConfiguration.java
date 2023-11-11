package com.recom.configuration;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.Locale;

@Slf4j
@Configuration
public class LocaleLoggerConfiguration {

    @PostConstruct
    public void init() {
        log.info("Locale initialized with '{}'", Locale.getDefault());
    }

}