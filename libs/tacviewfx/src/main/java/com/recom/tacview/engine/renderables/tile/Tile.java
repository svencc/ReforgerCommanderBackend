package com.recom.tacview.engine.renderables.tile;

import com.recom.tacview.engine.graphics.buffer.PixelBuffer;
import com.recom.tacview.engine.renderables.HasPixelBuffer;
import com.recom.tacview.engine.renderables.Positionable;
import com.recom.tacview.engine.renderables.sprite.Sprite;
import com.recom.tacview.engine.units.PixelCoordinate;
import lombok.NonNull;

public interface Tile extends HasPixelBuffer, Positionable {

    @NonNull
    PixelCoordinate getPosition();

    @NonNull
    Sprite getSprite();

    @NonNull
    PixelBuffer getPixelBuffer();

}
