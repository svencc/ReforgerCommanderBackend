package com.recom.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Data
@ConfigurationProperties(prefix = "recom.security")
public class RECOMSecurityProperties {

    private String jwtIssuer;
    private Duration jwtExpirationTime;

}
