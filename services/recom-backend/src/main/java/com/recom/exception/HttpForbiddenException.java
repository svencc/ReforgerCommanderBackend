package com.recom.exception;

import lombok.NonNull;

public class HttpForbiddenException extends RuntimeException {
    public HttpForbiddenException(@NonNull final String message) {
        super(message);
    }
}