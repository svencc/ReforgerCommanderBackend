package com.recom.commons.calculator;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ARGBCalculator {

    public static int blend(final int foregroundColor, final int backgroundColour) {
        final double alpha = getAlphaComponent(foregroundColor) / 255.0;
        final double oneMinusAlpha = 1 - alpha;

        final double newRed = ((getRedComponent(foregroundColor) * alpha) + (oneMinusAlpha * getRedComponent(backgroundColour)));
        final double newGreen = ((getGreenComponent(foregroundColor) * alpha) + (oneMinusAlpha * getGreenComponent(backgroundColour)));
        final double newBlue = ((getBlueComponent(foregroundColor) * alpha) + (oneMinusAlpha * getBlueComponent(backgroundColour)));
        final int newAlpha = getAlphaComponent(backgroundColour);

        return compose(newAlpha, (int) newRed, (int) newGreen, (int) newBlue);
    }

    public static int getAlphaComponent(final int color) {
        return (color >> 24) & 0xff;
    }

    public static int getRedComponent(final int color) {
        return (color >> 16) & 0xff;
    }

    public static int getGreenComponent(final int color) {
        return (color >> 8) & 0xff;
    }

    public static int getBlueComponent(final int color) {
        return color & 0xff;
    }

    public static int compose(final int alpha, final int red, final int green, final int blue) {
        return ((alpha & 0xFF) << 24) |
                ((red & 0xFF) << 16) |
                ((green & 0xFF) << 8) |
                ((blue & 0xFF << 0));
    }

}
