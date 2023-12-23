package com.recom.commander.property.restclient;

import com.recom.commander.event.InitializeComponentsEvent;
import com.recom.commander.model.Provideable;
import com.recom.commander.property.RestClientProperties;
import com.recom.commander.property.user.HostProperties;
import com.recom.observer.Notification;
import com.recom.observer.ObserverTemplate;
import com.recom.observer.ReactiveObserver;
import com.recom.observer.Subjective;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class RECOMUnauthenticatedRestClientProvider extends ObserverTemplate<HostProperties> implements Provideable<RestClient> {

    @NonNull
    private final RestClientProperties restClientProperties;
    @NonNull
    private final HostProperties hostProperties;


    @Nullable
    private ReactiveObserver<HostProperties> hostPropertiesReactiveObserver;
    @Nullable
    private RestClient restClient;


    @EventListener(InitializeComponentsEvent.class)
    public void init(@NonNull final InitializeComponentsEvent event) {
        event.logComponentInitialization(log, this.getClass());
        hostPropertiesReactiveObserver = ReactiveObserver.reactWith((__, ___) -> {
            log.info("HostProperties changed. Update {}.", this.getClass().getSimpleName());
            createNewRestClient();
        });
        hostPropertiesReactiveObserver.observe(hostProperties.getSubject());
    }

    @NonNull
    private RestClient createNewRestClient() {
        final SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(restClientProperties.getConnectTimeout().toMillisPart());
        requestFactory.setReadTimeout(restClientProperties.getReadTimeout().toMillisPart());

        return RestClient.builder()
                .requestFactory(requestFactory)
                .baseUrl(hostProperties.getHostBasePath())
                .build();
    }

    @NonNull
    public RestClient provide() {
        if (restClient == null) {
            restClient = createNewRestClient();
        }

        return restClient;
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
