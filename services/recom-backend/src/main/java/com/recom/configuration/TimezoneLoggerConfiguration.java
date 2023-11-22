package com.recom.configuration;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

@Slf4j
@Configuration
public class TimezoneLoggerConfiguration {

    @PostConstruct
    public void init() {
        log.info("Timezone initialized with '{}'", TimeZone.getDefault().getDisplayName());
    }

}