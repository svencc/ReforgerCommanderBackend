package com.recom.tacview.engine.components.tile;

import com.recom.tacview.engine.components.HasPixelBuffer;
import com.recom.tacview.engine.components.Positionable;
import com.recom.tacview.engine.components.sprite.Sprite;
import com.recom.tacview.engine.graphics.buffer.PixelBuffer;
import com.recom.tacview.engine.units.PixelCoordinate;

public interface Tile extends HasPixelBuffer, Positionable {

    PixelCoordinate getPosition();

    Sprite getSprite();

    PixelBuffer getPixelBuffer();

}
