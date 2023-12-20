package com.recom.commander.property.restclient;

import com.recom.commander.event.InitEvent;
import com.recom.commander.property.RestClientProperties;
import com.recom.commander.property.user.HostProperties;
import com.recom.commander.service.authentication.AuthenticationService;
import com.recom.dto.authentication.AuthenticationResponseDto;
import com.recom.dynamicproperties.exception.InitializationException;
import com.recom.observer.ReactiveObserver;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class RECOMRestClientProvider {

    @NonNull
    private final RestClientProperties restClientProperties;
    @NonNull
    private final HostProperties hostProperties;
    @NonNull
    private final ObjectProvider<AuthenticationService> authenticationServiceProvider;

    @Nullable
    private ReactiveObserver<HostProperties> hostPropertiesReactiveObserver;
    @Nullable
    private ReactiveObserver<AuthenticationResponseDto> authenticationReactiveObserver;
    @Nullable
    private RestClient restClient;


    @EventListener(InitEvent.class)
    public void init(@NonNull final InitEvent event) {
        event.log(log, this.getClass());
        authenticationReactiveObserver = ReactiveObserver.reactWith((__, ___) -> {
            log.info("Authentication changed. Update RestClient.");
            createNewRestClient();
        });
        try {
            authenticationReactiveObserver.observe(authenticationServiceProvider.getIfAvailable().getBufferedSubject());
        } catch (final BeansException e) {
            throw new InitializationException("AuthenticationService not available!");
        }

        hostPropertiesReactiveObserver = ReactiveObserver.reactWith((__, ___) -> {
            log.info("HostProperties changed. Update RestClient.");
            createNewRestClient();
        });
        hostPropertiesReactiveObserver.observe(hostProperties.getSubject());
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
                .defaultHeader(HttpHeaders.AUTHORIZATION, authenticationServiceProvider.getIfAvailable().provideBearerToken())
                .build();
    }

}
