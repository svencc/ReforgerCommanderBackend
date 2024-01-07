package com.recom.commander.initializr;


import com.recom.commander.event.InitialAuthenticationEvent;
import com.recom.commander.event.StageReadyEvent;
import com.recom.commander.service.authentication.AuthenticationService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationInitializer {

    @NonNull
    private final AuthenticationService authenticationService;
    @NonNull
    private final ApplicationContext applicationContext;


    @EventListener(classes = StageReadyEvent.class)
    public void initialize(@NonNull final StageReadyEvent event) {
        try {
            authenticationService.authenticate();
            event.logStageInitializationWithMessage(log, AuthenticationInitializer.class, "Authentication successful");
            applicationContext.publishEvent(new InitialAuthenticationEvent(this)); // maybe move to authentication service -> then call it on first successful authentication ever
        } catch (final Throwable t) {
            event.logStageInitializationErrorWithMessage(log, AuthenticationInitializer.class, String.format("Authentication failed: %1s", t.getMessage()));
        }
    }

}
