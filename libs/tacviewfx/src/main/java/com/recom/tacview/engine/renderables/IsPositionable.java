package com.recom.tacview.engine.renderables;

import com.recom.commons.units.PixelCoordinate;
import lombok.NonNull;

public interface IsPositionable {

    @NonNull
    PixelCoordinate getPosition();

}
