package com.recom.commander.property;

import com.recom.commander.property.gateway.RECOMHostProperties;
import com.recom.commander.property.user.HostProperties;
import com.recom.dynamicproperties.PropertyBinder;
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
            @NonNull final RECOMHostProperties recomHostProperties,
            @NonNull final PropertyBinder propertyBinder
    ) {
        final HostProperties hostProperties = new HostProperties();
        propertyBinder.bindToFilesystem(hostProperties);

        final SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(restClientProperties.getConnectTimeout().toMillisPart());
        factory.setReadTimeout(restClientProperties.getReadTimeout().toMillisPart());

        return RestClient.builder()
                .requestFactory(factory)
                .baseUrl(recomHostProperties.getHostBasePath())
                .build();
    }

}
