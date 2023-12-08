package com.recom.commander.exception;

import lombok.NonNull;
import org.springframework.lang.Nullable;

public class HttpMethodNotAllowedException extends HttpErrorException {

    public HttpMethodNotAllowedException(
            @NonNull final String message,
            @Nullable final String responseBody
    ) {
        super(message,responseBody);
    }

}
