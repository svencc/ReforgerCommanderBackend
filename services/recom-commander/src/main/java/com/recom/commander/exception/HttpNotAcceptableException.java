package com.recom.commander.exception;

import lombok.NonNull;
import org.springframework.lang.Nullable;

public class HttpNotAcceptableException extends HttpErrorException {

    public HttpNotAcceptableException(
            @NonNull final String message,
            @Nullable final String responseBody
    ) {
        super(message,responseBody);
    }

}
