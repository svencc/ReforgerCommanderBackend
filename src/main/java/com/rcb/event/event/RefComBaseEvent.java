package com.rcb.event.event;

import lombok.Getter;
import lombok.NonNull;

import java.util.Date;

public abstract class RefComBaseEvent {

    @Getter
    @NonNull
    public final Date creationDate = new Date();

}
