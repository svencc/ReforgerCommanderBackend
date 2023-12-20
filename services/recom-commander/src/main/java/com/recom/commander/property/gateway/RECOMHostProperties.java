package com.recom.commander.property.gateway;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties("recom.host")
public class RECOMHostProperties {

    private String protocol;
    private String hostname;
    private String port;
    private String hostBasePath;

}
