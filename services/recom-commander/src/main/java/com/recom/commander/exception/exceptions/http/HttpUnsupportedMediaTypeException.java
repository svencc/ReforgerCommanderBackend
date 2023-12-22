package com.recom.commander.exception.exceptions.http;

import lombok.NonNull;
import org.springframework.lang.Nullable;

public class HttpUnsupportedMediaTypeException extends HttpErrorException {

    public HttpUnsupportedMediaTypeException(
            @NonNull final String message,
            @Nullable final String nullableResponseBody
    ) {
        super(message, nullableResponseBody);
    }

}
