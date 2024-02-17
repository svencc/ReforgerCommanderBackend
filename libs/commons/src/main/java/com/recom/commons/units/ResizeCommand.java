package com.recom.commons.units;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ResizeCommand {

    @NonNull
    private final PixelDimension pixelDimension;
    private final int newScale;


    public static ResizeCommand of(
            @NonNull final PixelDimension pixelDimension,
            final int renderScale
    ) {
        return new ResizeCommand(pixelDimension, renderScale);
    }

}
