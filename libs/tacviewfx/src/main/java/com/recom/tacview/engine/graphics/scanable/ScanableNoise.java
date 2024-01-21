package com.recom.tacview.engine.graphics.scanable;

import com.recom.tacview.engine.graphics.IsScanable;
import com.recom.tacview.engine.units.PixelDimension;
import com.recom.tacview.service.RandomProvider;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ScanableNoise implements IsScanable {

    @NonNull
    private final RandomProvider randomProvider;
    @Getter
    @NonNull
    private final PixelDimension dimension;

    @Override
    public int scanPixelAt(
            final int x,
            final int y
    ) {
        return randomProvider.provide().nextInt() * (randomProvider.provide().nextInt(5) / 4);
    }

    @Override
    public int scanPixelAtIndex(final int index) {
        return randomProvider.provide().nextInt() * (randomProvider.provide().nextInt(5) / 4);
    }

}
