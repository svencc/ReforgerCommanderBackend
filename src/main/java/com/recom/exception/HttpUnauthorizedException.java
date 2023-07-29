package com.recom.exception;

import lombok.NonNull;

public class HttpUnauthorizedException extends RuntimeException {
    public HttpUnauthorizedException(@NonNull final String message) {
        super(message);
    }
}