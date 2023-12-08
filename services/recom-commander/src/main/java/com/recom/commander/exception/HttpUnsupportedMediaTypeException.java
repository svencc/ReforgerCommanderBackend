package com.recom.commander.exception;

import lombok.NonNull;
import org.springframework.lang.Nullable;

public class HttpUnsupportedMediaTypeException extends HttpErrorException {

    public HttpUnsupportedMediaTypeException(
            @NonNull final String message,
            @Nullable final String responseBody
    ) {
        super(message,responseBody);
    }

}
