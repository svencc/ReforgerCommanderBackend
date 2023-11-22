package com.recom.exception;

import lombok.NonNull;

public class HttpTimeoutException extends RuntimeException {
    public HttpTimeoutException(@NonNull final String message) {
        super(message);
    }
}