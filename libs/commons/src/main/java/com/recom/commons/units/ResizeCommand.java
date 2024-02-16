package com.recom.commons.units;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ResizeCommand {

    @NonNull
    private final PixelDimension scaledPixelDimension;
    private final int newScale;


    public static ResizeCommand of(
            @NonNull final PixelDimension scaledPixelDimension,
            final int newScale
    ) {
        return new ResizeCommand(scaledPixelDimension, newScale);
    }

}
