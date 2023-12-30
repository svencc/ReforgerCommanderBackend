package com.recom.commander.service.authentication;

import com.recom.commander.exception.exceptions.http.HttpErrorException;
import com.recom.commander.property.user.AuthenticationProperties;
import com.recom.commander.service.Scheduler;
import com.recom.dto.authentication.AuthenticationRequestDto;
import com.recom.dto.authentication.AuthenticationResponseDto;
import com.recom.observer.*;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Objects;

@Slf4j
@Service
public class AuthenticationService extends ObserverTemplate<AuthenticationResponseDto> implements HasBufferedSubject<AuthenticationResponseDto>, AutoCloseable {

    @NonNull
    private final Scheduler scheduler;
    @NonNull
    private final AuthenticationGateway authenticationGateway;
    @NonNull
    private final AuthenticationProperties authenticationProperties;
    @NonNull
    private final BufferedSubject<AuthenticationResponseDto> subject;

    @Nullable
    private ReactiveObserver<AuthenticationProperties> authenticationPropertiesReactiveObserver;


    public AuthenticationService(
            @NonNull final Scheduler scheduler,
            @NonNull final AuthenticationGateway authenticationGateway,
            @NonNull final AuthenticationProperties authenticationProperties
    ) {
        this.scheduler = scheduler;
        this.authenticationGateway = authenticationGateway;
        this.authenticationProperties = authenticationProperties;
        subject = new BufferedSubject<>();
        subject.beObservedBy(this);
        init();
    }

    public void init() {
        authenticationPropertiesReactiveObserver = ReactiveObserver.reactWith((__, ___) -> {
            log.info("AuthenticationProperties changed. Trigger re-authentication.");
            authenticate();
        });
        authenticationPropertiesReactiveObserver.observe(authenticationProperties.getSubject());
    }

    public void authenticate() throws HttpErrorException {
        final AuthenticationRequestDto authenticationRequest = AuthenticationRequestDto.builder()
                .accountUUID(authenticationProperties.getAccountUUID())
                .accessKey(authenticationProperties.getAccessKey())
                .build();
        final AuthenticationResponseDto authentication = authenticationGateway.authenticate(authenticationRequest);
        subject.notifyObserversWith(Notification.of(authentication));
    }

    @NonNull
    public String provideBearerToken() {
        return String.format("Bearer %1s", provideAuthenticationToken());
    }

    @NonNull
    public String provideAuthenticationToken() {
        return getBufferedSubject().getLastBufferedNotification()
                .map(Notification::getPayload)
                .map(AuthenticationResponseDto::getToken)
                .filter(Objects::nonNull)
                .orElse("<not authenticated>");

    }

    @NonNull
    @Override
    public BufferedSubject<AuthenticationResponseDto> getBufferedSubject() {
        return subject;
    }

    @Override
    public void takeNotice(
            @NonNull final Subjective<AuthenticationResponseDto> subject,
            @NonNull final Notification<AuthenticationResponseDto> notification
    ) {
        final Duration expiresIn = Duration.ofSeconds(notification.getPayload().getExpiresInSeconds());
        final Duration delayToReauthenticate = expiresIn.minus(authenticationProperties.getReAuthenticateInAdvance());
        scheduler.schedule(this::authenticate, delayToReauthenticate);
    }

    @Override
    public void close() {
        super.close();
        if (authenticationPropertiesReactiveObserver != null) {
            authenticationPropertiesReactiveObserver.close();
        }
    }

}
