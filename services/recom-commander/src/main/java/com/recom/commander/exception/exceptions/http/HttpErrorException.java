package com.recom.commander.exception.exceptions.http;

import lombok.Getter;
import lombok.NonNull;
import org.springframework.lang.Nullable;

import java.util.Optional;

@Getter
public class HttpErrorException extends RuntimeException {

    @NonNull
    private final Optional<String> maybeResponseBody;

    public HttpErrorException(
            @NonNull final String message,
            @Nullable final String nullableResponseBody
    ) {
        super(message);
        this.maybeResponseBody = Optional.ofNullable(nullableResponseBody);
    }

    public HttpErrorException(
            @NonNull final String message,
            @Nullable final Throwable cause
    ) {
        super(message, cause);
        this.maybeResponseBody = Optional.empty();
    }

}
