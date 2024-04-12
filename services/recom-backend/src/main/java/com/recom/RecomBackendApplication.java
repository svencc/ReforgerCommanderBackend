package com.recom;

import com.recom.property.RECOMAsyncProperties;
import com.recom.property.RECOMSecurityProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import java.util.Locale;
import java.util.TimeZone;

@EnableScheduling
@SpringBootApplication(scanBasePackages = {"com.recom"})
@EnableConfigurationProperties({
        RECOMSecurityProperties.class,
        RECOMAsyncProperties.class
})
public class RecomBackendApplication {

    public static void main(String[] args) {
        // Set default locale to US
        Locale.setDefault(Locale.US);
        // Set default timezone to UTC
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        // Start Spring Boot Application
        final ConfigurableApplicationContext context = SpringApplication.run(RecomBackendApplication.class, args);

        // Start Spring Context
        context.start();
    }

    // SET in application.properties -> logging.level.org.springframework.web.filter.CommonsRequestLoggingFilter: DEBUG
    @Bean
    @Profile("local")
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        final CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
        loggingFilter.setIncludeClientInfo(true);
        loggingFilter.setIncludeQueryString(true);
        loggingFilter.setIncludeHeaders(true);
        loggingFilter.setIncludePayload(true);
        loggingFilter.setMaxPayloadLength(64000);

        return loggingFilter;
    }

}
