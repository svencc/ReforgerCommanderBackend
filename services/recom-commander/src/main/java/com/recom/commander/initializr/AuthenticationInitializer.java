package com.recom.commander.initializr;


import com.recom.commander.event.StageReadyEvent;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationInitializer {


    @EventListener(classes = StageReadyEvent.class)
    public void initialize(@NonNull final StageReadyEvent event) {
        event.logStageInitialization(log, AuthenticationInitializer.class);
        log.info("... Initializing Authentication ...");
    }

}
