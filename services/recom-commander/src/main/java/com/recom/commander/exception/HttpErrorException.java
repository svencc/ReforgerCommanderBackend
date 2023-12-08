package com.recom.commander.exception;

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
            @Nullable final String maybeResponseBody
    ) {
        super(message);
        this.maybeResponseBody = Optional.ofNullable(maybeResponseBody);
    }

}
