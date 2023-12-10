package com.recom.dynamicproperties;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
class Assert {

    public void isTrue(
            final boolean condition,
            @NonNull final String message
    ) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }

}
