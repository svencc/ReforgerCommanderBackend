package com.rcb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@EnableCaching
@SpringBootApplication(scanBasePackages = {"com.rcb"})
@EnableConfigurationProperties({
})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    // Uncomment to log all requests to console
    // SET in application.yml -> logging.level.org.springframework.web.filter.CommonsRequestLoggingFilter: DEBUG
//    @Bean
//    @Profile("local")
//    public CommonsRequestLoggingFilter requestLoggingFilter() {
//        CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
//        loggingFilter.setIncludeClientInfo(true);
//        loggingFilter.setIncludeQueryString(true);
//        loggingFilter.setIncludeHeaders(true);
//        loggingFilter.setIncludePayload(true);
//        loggingFilter.setMaxPayloadLength(64000);
//
//        return loggingFilter;
//    }

}
