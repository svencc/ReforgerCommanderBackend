package com.recom.dynamicproperties;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
class Exceptional {

    @NonNull
    public IllegalStateException whenPropertyNotSet(@NonNull final String property) {
        return new IllegalStateException(String.format("%1s not set", property));
    }

}
