package com.recom.commander.exception.exceptions.http;

import lombok.NonNull;
import org.springframework.lang.Nullable;

public class HttpNotAcceptableException extends HttpErrorException {

    public HttpNotAcceptableException(
            @NonNull final String message,
            @Nullable final String nullableResponseBody
    ) {
        super(message, nullableResponseBody);
    }

}
