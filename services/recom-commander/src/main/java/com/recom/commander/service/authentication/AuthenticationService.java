package com.recom.commander.service.authentication;

import com.recom.commander.property.user.AuthenticationProperties;
import com.recom.commander.service.Scheduler;
import com.recom.dto.authentication.AuthenticationResponseDto;
import com.recom.observer.*;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Objects;

@Service
public class AuthenticationService extends ObserverTemplate<AuthenticationResponseDto> implements HasBufferedSubject<AuthenticationResponseDto> {

    @NonNull
    private final Scheduler scheduler;
    @NonNull
    private final AuthenticationGateway authenticationGateway;
    @NonNull
    private final AuthenticationProperties authenticationProperties;
    @NonNull
    private final BufferedSubject<AuthenticationResponseDto> subject;


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
    }

    public void authenticate() {
        final AuthenticationResponseDto authenticate = authenticationGateway.authenticate();
        subject.notifyObserversWith(Notification.of(authenticate));
    }

    @NonNull
    @Override
    public BufferedSubject<AuthenticationResponseDto> getBufferedSubject() {
        return subject;
    }

    @NonNull
    public String provideAuthenticationToken() {
        return getBufferedSubject().getLastBufferedNotification()
                .map(Notification::getPayload)
                .map(AuthenticationResponseDto::getToken)
                .filter(Objects::nonNull)
                .orElse("<not authenticated>");

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

}
