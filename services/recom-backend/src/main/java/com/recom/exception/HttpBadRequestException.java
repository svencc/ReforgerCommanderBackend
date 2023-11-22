package com.recom.exception;

import lombok.NonNull;

public class HttpBadRequestException extends RuntimeException {
    public HttpBadRequestException(@NonNull final String message) {
        super(message);
    }

}