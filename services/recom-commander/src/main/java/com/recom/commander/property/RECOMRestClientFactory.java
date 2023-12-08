package com.recom.commander.property;

import com.recom.commander.property.gateway.RECOMHostProperties;
import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RECOMRestClientFactory {

    @Bean("recomRestClient")
    public RestClient createRestClient(
            @NonNull final RestClientProperties restClientProperties,
            @NonNull final RECOMHostProperties recomHostProperties
    ) {
        final SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(restClientProperties.getConnectTimeout().toMillisPart());
        factory.setReadTimeout(restClientProperties.getReadTimeout().toMillisPart());

        return RestClient.builder()
                .requestFactory(factory)
                .baseUrl(recomHostProperties.getHostBasePath())
                .build();
    }

}
