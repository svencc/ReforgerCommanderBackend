package com.recom.commander.exception.exceptions.http;

import lombok.NonNull;
import org.springframework.lang.Nullable;

public class HttpUnauthorizedException extends HttpErrorException {

    public HttpUnauthorizedException(
            @NonNull final String message,
            @Nullable final String nullableResponseBody
    ) {
        super(message, nullableResponseBody);
    }

}
