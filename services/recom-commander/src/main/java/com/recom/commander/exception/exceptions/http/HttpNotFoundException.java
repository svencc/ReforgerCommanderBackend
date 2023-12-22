package com.recom.commander.exception.exceptions.http;

import lombok.NonNull;
import org.springframework.lang.Nullable;

public class HttpNotFoundException extends HttpErrorException {

    public HttpNotFoundException(
            @NonNull final String message,
            @Nullable final String nullableResponseBody
    ) {
        super(message, nullableResponseBody);
    }

}
