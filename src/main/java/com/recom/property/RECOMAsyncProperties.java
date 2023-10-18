package com.recom.property;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "recom.async")
public class RECOMAsyncProperties {

    private Integer maxPoolSize;
    private Integer corePoolSize;

}
