package com.recom.tacview.engine.renderables.tile;

import com.recom.tacview.engine.graphics.buffer.PixelBuffer;
import com.recom.tacview.engine.renderables.HasPixelBuffer;
import com.recom.tacview.engine.renderables.IsPositionable;
import com.recom.tacview.engine.renderables.sprite.Sprite;
import com.recom.commons.units.PixelCoordinate;
import lombok.NonNull;

public interface IsTile extends HasPixelBuffer, IsPositionable {

    @NonNull
    PixelCoordinate getPosition();

    @NonNull
    Sprite getSprite();

    @NonNull
    PixelBuffer getPixelBuffer();

}
