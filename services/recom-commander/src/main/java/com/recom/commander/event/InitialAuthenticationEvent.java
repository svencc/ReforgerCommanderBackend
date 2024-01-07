package com.recom.commander.event;

import lombok.NonNull;
import org.springframework.context.ApplicationEvent;

public class InitialAuthenticationEvent extends ApplicationEvent {

    public InitialAuthenticationEvent(@NonNull final Object source) {
        super(source);
    }

}
