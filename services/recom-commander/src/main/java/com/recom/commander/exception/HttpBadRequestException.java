package com.recom.commander.exception;

import lombok.NonNull;
import org.springframework.lang.Nullable;

import java.util.Optional;

public class HttpBadRequestException extends HttpErrorException {

    public HttpBadRequestException(
            @NonNull final String message,
            @Nullable final String responseBody
    ) {
        super(message,responseBody);
    }

}
