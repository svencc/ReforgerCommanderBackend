package com.recom.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Optional;

@Data
@ConfigurationProperties(prefix = "recom.security")
public class RECOMSecurityProperties {

    private String jwtIssuer;
    private Duration jwtExpirationTime;
    private Optional<String> keyPath;

    public Optional<String> getKeyPath() {
        if (keyPath.isPresent() && !keyPath.get().isEmpty()) {
            return Optional.of(keyPath.get());
        } else {
            return Optional.empty();
        }
    }

}
