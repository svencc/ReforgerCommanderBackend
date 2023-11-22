package com.recom.util;

import lombok.experimental.UtilityClass;
import org.springframework.lang.Nullable;

@UtilityClass
public class StringUtil {

    public boolean isNumeric(@Nullable final String string) {
        return string != null && !string.isEmpty();
    }

}
