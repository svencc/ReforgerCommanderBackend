package com.recom.commander.property;

import com.recom.commander.property.user.HostProperties;
import com.recom.observer.Notification;
import com.recom.observer.ObserverTemplate;
import com.recom.observer.Subjective;
import lombok.NonNull;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class RECOMRestClientProvider extends ObserverTemplate<HostProperties> {

    @NonNull
    private final RestClientProperties restClientProperties;
    @NonNull
    private final HostProperties hostProperties;


    @Nullable
    private RestClient restClient;


    public RECOMRestClientProvider(
            @NonNull final RestClientProperties restClientProperties,
            @NonNull final HostProperties hostProperties
    ) {
        this.restClientProperties = restClientProperties;
        this.hostProperties = hostProperties;

        hostProperties.getSubject().beObservedBy(this);
    }

    @NonNull
    public RestClient provide() {
        if (restClient == null) {
            restClient = createRestClient();
        }

        return restClient;
    }

    @NonNull
    private RestClient createRestClient() {
        final SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(restClientProperties.getConnectTimeout().toMillisPart());
        factory.setReadTimeout(restClientProperties.getReadTimeout().toMillisPart());

        return RestClient.builder()
                .requestFactory(factory)
                .baseUrl(hostProperties.getHostBasePath())
                .build();
    }

    @Override
    public void takeNotice(@NonNull Subjective<HostProperties> subject, @NonNull Notification<HostProperties> notification) {
        restClient = createRestClient();
    }

}
