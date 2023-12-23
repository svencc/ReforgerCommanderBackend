package com.recom.commander.exception.exceptions.http;

import lombok.NonNull;

public class HttpNoConnectionException extends HttpErrorException {

    public HttpNoConnectionException(
            @NonNull final String message,
            @NonNull final Throwable cause
    ) {
        super(message, cause);
    }

    public HttpNoConnectionException(
            @NonNull final Class clazz,
            @NonNull final Throwable cause
    ) {
        super(createMessage(clazz), cause);
    }

    @NonNull
    private static String createMessage(@NonNull final Class clazz) {
        return String.format("Gateway (%1s) no connection while requesting data!", clazz.getSimpleName());
    }

}
