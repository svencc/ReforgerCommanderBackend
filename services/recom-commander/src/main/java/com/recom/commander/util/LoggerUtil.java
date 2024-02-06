package com.recom.commander.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LoggerUtil {

    public String generateCenteredString(@NonNull final String text) {
        final int totalWidth = 80;
        final int textWidth = text.length();

        if (textWidth >= totalWidth) {
            return text;
        }

        final int padding = (totalWidth - textWidth) / 2;
        final StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("-".repeat(padding));
        stringBuilder.append(' ').append(text).append(' ');
        stringBuilder.append("-".repeat(Math.max(0, totalWidth - stringBuilder.length())));

        return stringBuilder.toString();
    }

}
