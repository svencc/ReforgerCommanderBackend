package com.recom.commons.calculator;

import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor
public class ARGBColor {

    @NonNull
    public static int RGB(
            final int r,
            final int g,
            final int b
    ) {
        return ARGB(255, r, g, b);
    }

    @NonNull
    public static int ARGB(
            final int a,
            final int r,
            final int g,
            final int b
    ) {
        if (a < 0 || a > 255 || r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255) {
            throw new IllegalArgumentException("Invalid color component value. Must be between 0 and 255.");
        } else {
            return ((a << 24) | (r << 16) | (g << 8) | b);
        }
    }

}
