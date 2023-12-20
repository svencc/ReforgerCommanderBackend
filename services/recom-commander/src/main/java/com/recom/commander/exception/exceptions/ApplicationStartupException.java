package com.recom.commander.exception.exceptions;

import lombok.NonNull;

public class ApplicationStartupException extends Exception {

    public ApplicationStartupException(@NonNull final String message) {
        super(message);
    }

}
