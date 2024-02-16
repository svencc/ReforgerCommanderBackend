package com.recom.commander.property.restclient;

import com.recom.commander.event.InitializeComponentsEvent;
import com.recom.commander.model.Provideable;
import com.recom.commander.property.RestClientProperties;
import com.recom.commander.property.user.DynamicHostProperties;
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
public class RECOMUnauthenticatedRestClientProvider extends ObserverTemplate<DynamicHostProperties> implements Provideable<RestClient>, AutoCloseable {

    @NonNull
    private final RestClientProperties restClientProperties;
    @NonNull
    private final DynamicHostProperties dynamicHostProperties;


    @Nullable
    private ReactiveObserver<DynamicHostProperties> hostPropertiesReactiveObserver;
    @Nullable
    private RestClient restClient;


    @EventListener(InitializeComponentsEvent.class)
    public void init(@NonNull final InitializeComponentsEvent event) {
        event.logComponentInitialization(log, this.getClass());
        hostPropertiesReactiveObserver = ReactiveObserver.reactWith((__, ___) -> {
            log.info("HostProperties changed. Update {}.", this.getClass().getSimpleName());
            createNewRestClient();
        });
        hostPropertiesReactiveObserver.observe(dynamicHostProperties.getSubject());
    }

    @NonNull
    private RestClient createNewRestClient() {
        final SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(restClientProperties.getConnectTimeout().toMillisPart());
        requestFactory.setReadTimeout(restClientProperties.getReadTimeout().toMillisPart());

        return RestClient.builder()
                .requestFactory(requestFactory)
                .baseUrl(dynamicHostProperties.getHostBasePath())
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
            @NonNull final Subjective<DynamicHostProperties> subject,
            @NonNull final Notification<DynamicHostProperties> notification
    ) {
        log.info("HostProperties changed. Creating new UnauthenticatedRestClient.");
        restClient = createNewRestClient();
    }

    @Override
    public void close() {
        super.close();
        if (hostPropertiesReactiveObserver != null) {
            hostPropertiesReactiveObserver.close();
        }
    }

}
