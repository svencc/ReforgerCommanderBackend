package com.recom.exception;

import lombok.NonNull;

public class HttpNotFoundException extends RuntimeException {
    public HttpNotFoundException(@NonNull final String message) {
        super(message);
    }
}