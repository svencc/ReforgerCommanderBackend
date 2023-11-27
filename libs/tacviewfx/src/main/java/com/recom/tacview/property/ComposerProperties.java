package com.recom.tacview.property;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Configuration
@ConfigurationProperties("renderer.composer")
public class ComposerProperties {

    private boolean parallelizedBackBufferHandler;
    private int backBufferSize;

}
