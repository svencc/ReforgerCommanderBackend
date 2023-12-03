package com.recom.commander.event;

import lombok.NonNull;
import org.springframework.context.ApplicationEvent;

public class ShutdownEvent extends ApplicationEvent {

    public ShutdownEvent(@NonNull final Object source) {
        super(source);
    }

}
