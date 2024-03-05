package com.recom.commons.calculator;

import com.recom.commons.math.Round;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
public class ARGBCalculator {

    @NonNull
    private static final Map<Long, Integer> colorCache = new HashMap<>();


    public int blend(final int foregroundColor, final int backgroundColour) {
        final long key = ((long) foregroundColor << 32) | (backgroundColour & 0xFFFFFFFFL);
        if (colorCache.containsKey(key)) {
            return colorCache.get(key);
        }

        final double alpha = getAlphaComponent(foregroundColor) / 255.0;
        final double oneMinusAlpha = 1 - alpha;

        final double newRed = ((getRedComponent(foregroundColor) * alpha) + (oneMinusAlpha * getRedComponent(backgroundColour)));
        final double newGreen = ((getGreenComponent(foregroundColor) * alpha) + (oneMinusAlpha * getGreenComponent(backgroundColour)));
        final double newBlue = ((getBlueComponent(foregroundColor) * alpha) + (oneMinusAlpha * getBlueComponent(backgroundColour)));
        final int newAlpha = getAlphaComponent(backgroundColour);

        int result = compose(newAlpha, (int) newRed, (int) newGreen, (int) newBlue);
        colorCache.put(key, result);

        return result;
    }

    public int getAlphaComponent(final int color) {
        return (color >> 24) & 0xff;
    }

    public int getRedComponent(final int color) {
        return (color >> 16) & 0xff;
    }

    public int getGreenComponent(final int color) {
        return (color >> 8) & 0xff;
    }

    public int getBlueComponent(final int color) {
        return color & 0xff;
    }

    public int compose(final int alpha, final int red, final int green, final int blue) {
        return ((alpha & 0xFF) << 24) |
                ((red & 0xFF) << 16) |
                ((green & 0xFF) << 8) |
                ((blue & 0xFF << 0));
    }

    public int modifyBrightness(
            final int baseColor,
            final double brightness
    ) {
        return compose(
                getAlphaComponent(baseColor),
                Round.halfUp(getRedComponent(baseColor) * brightness),
                Round.halfUp(getGreenComponent(baseColor) * brightness),
                Round.halfUp(getBlueComponent(baseColor) * brightness)
        );
    }

    public int modifyTransparency(
            final int baseColor,
            final double alphaFactor
    ) {
        return compose(
                Round.halfUp(getAlphaComponent(baseColor) * alphaFactor),
                getRedComponent(baseColor),
                getGreenComponent(baseColor),
                getBlueComponent(baseColor)
        );
    }

}
