package com.recom.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "recom.async")
public class RECOMAsyncProperties {

    private Integer maxPoolSize;
    private Integer corePoolSize;

}
