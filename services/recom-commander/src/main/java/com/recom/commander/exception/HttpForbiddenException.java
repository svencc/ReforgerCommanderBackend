package com.recom.commander.exception;

import lombok.NonNull;
import org.springframework.lang.Nullable;

public class HttpForbiddenException extends HttpErrorException {

    public HttpForbiddenException(
            @NonNull final String message,
            @Nullable final String responseBody
    ) {
        super(message,responseBody);
    }

}
