package com.recom.property;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "recom.security")
public class RECOMSecurityProperties {

    private String jwtIssuer;
    private Duration jwtExpirationTime;
    private Optional<String> keyPath;

    public Optional<Path> getKeyPath() {
        if (keyPath.isPresent() && !keyPath.get().isEmpty()) {
            return Optional.of(Path.of(keyPath.get()));
        } else {
            return Optional.empty();
        }
    }

}
