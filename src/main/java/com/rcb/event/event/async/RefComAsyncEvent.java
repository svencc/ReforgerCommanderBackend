package com.rcb.event.event.async;

import com.rcb.event.event.RefComBaseEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Date;

@Getter
@NoArgsConstructor
public class RefComAsyncEvent extends RefComBaseEvent {

    @NonNull
    private final Date creationDate = new Date();

}
