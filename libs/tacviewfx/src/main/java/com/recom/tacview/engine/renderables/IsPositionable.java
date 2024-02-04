package com.recom.tacview.engine.renderables;

import com.recom.tacview.engine.units.PixelCoordinate;
import lombok.NonNull;

public interface IsPositionable {

    @NonNull
    PixelCoordinate getPosition();

}
