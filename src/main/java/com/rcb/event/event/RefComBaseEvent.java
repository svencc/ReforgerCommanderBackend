package com.rcb.event.event;

import lombok.NonNull;

import java.util.Date;

public abstract class RefComBaseEvent {

    @NonNull
    private final Date creationDate = new Date();

}
