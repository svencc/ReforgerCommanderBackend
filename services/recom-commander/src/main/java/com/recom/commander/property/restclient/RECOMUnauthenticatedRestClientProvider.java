package com.recom.commander.property.restclient;

import com.recom.commander.property.RestClientProperties;
import com.recom.commander.property.user.HostProperties;
import com.recom.observer.Notification;
import com.recom.observer.ObserverTemplate;
import com.recom.observer.Subjective;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
public class RECOMUnauthenticatedRestClientProvider extends ObserverTemplate<HostProperties> {

    @NonNull
    private final RestClientProperties restClientProperties;
    @NonNull
    private final HostProperties hostProperties;


    @Nullable
    private RestClient restClient;

    //TODO <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    //TODO rework this class

    public RECOMUnauthenticatedRestClientProvider(
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
            restClient = createNewRestClient();
        }

        return restClient;
    }

    @NonNull
    private RestClient createNewRestClient() {
        final SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(restClientProperties.getConnectTimeout().toMillisPart());
        factory.setReadTimeout(restClientProperties.getReadTimeout().toMillisPart());

        return RestClient.builder()
                .requestFactory(factory)
                .baseUrl(hostProperties.getHostBasePath())
                .build();
    }

    @Override
    public void takeNotice(
            @NonNull final Subjective<HostProperties> subject,
            @NonNull final Notification<HostProperties> notification
    ) {
        log.info("HostProperties changed. Creating new UnauthenticatedRestClient.");
        restClient = createNewRestClient();
    }


}
