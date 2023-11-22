package com.recom.exception;

import lombok.NonNull;

public class DBCachedDeserializationException extends RuntimeException {
    public DBCachedDeserializationException(@NonNull final String message, @NonNull final Exception cause) {
        super(message, cause);
    }
}
