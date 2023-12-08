package com.recom.commander.exception;

import lombok.NonNull;
import org.springframework.lang.Nullable;

public class HttpInternalServerErrorException extends HttpErrorException {

    public HttpInternalServerErrorException(
            @NonNull final String message,
            @Nullable final String responseBody
    ) {
        super(message,responseBody);
    }

}
