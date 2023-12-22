package com.recom.commander.exception.exceptions.http;

import lombok.NonNull;
import org.springframework.lang.Nullable;

public class HttpForbiddenException extends HttpErrorException {

    public HttpForbiddenException(
            @NonNull final String message,
            @Nullable final String nullableResponseBody
    ) {
        super(message, nullableResponseBody);
    }

}
