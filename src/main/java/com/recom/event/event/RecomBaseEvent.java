package com.recom.event.event;

import lombok.Getter;
import lombok.NonNull;

import java.util.Date;

public abstract class RecomBaseEvent {

    @Getter
    @NonNull
    public final Date creationDate = new Date();

}
