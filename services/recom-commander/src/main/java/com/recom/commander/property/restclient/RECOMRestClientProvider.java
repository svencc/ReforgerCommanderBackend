package com.recom.commander.property.restclient;

import com.recom.commander.property.RestClientProperties;
import com.recom.commander.property.user.HostProperties;
import com.recom.commander.service.authentication.AuthenticationService;
import com.recom.dto.authentication.AuthenticationResponseDto;
import com.recom.observer.ReactiveObserver;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
public class RECOMRestClientProvider {

    @NonNull
    private final RestClientProperties restClientProperties;
    @NonNull
    private final HostProperties hostProperties;
    @NonNull
    private final AuthenticationService authenticationService;
    @NonNull
    private final ReactiveObserver<HostProperties> hostPropertiesReactiveObserver;
    @NonNull
    private final ReactiveObserver<AuthenticationResponseDto> authenticationReactiveObserver;


    @Nullable
    private RestClient restClient;


    public RECOMRestClientProvider(
            @NonNull final RestClientProperties restClientProperties,
            @NonNull final HostProperties hostProperties,
            @NonNull final AuthenticationService authenticationService
    ) {
        this.restClientProperties = restClientProperties;
        this.hostProperties = hostProperties;
        this.authenticationService = authenticationService;

        authenticationReactiveObserver = ReactiveObserver.reactWith((__, ___) -> {
            log.info("Authentication changed. Update RestClient.");
            createNewRestClient();
        });
        authenticationReactiveObserver.observe(authenticationService.getBufferedSubject());

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
                .defaultHeader(HttpHeaders.AUTHORIZATION, authenticationService.provideAuthenticationToken())
                .build();
    }

}
