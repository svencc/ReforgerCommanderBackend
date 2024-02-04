package com.recom.tacview.engine.graphics;


import com.recom.tacview.engine.units.PixelDimension;
import lombok.NonNull;

public interface IsScanable {

    @NonNull
    PixelDimension getDimension();

    int scanPixelAt(
            final int x,
            final int y
    );

    int scanPixelAtIndex(final int index);

}
