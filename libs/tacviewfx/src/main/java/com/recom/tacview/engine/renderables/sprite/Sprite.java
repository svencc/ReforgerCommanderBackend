package com.recom.tacview.engine.renderables.sprite;

import com.recom.tacview.engine.renderables.HasPixelBuffer;
import com.recom.tacview.engine.graphics.buffer.PixelBuffer;
import com.recom.commons.units.PixelDimension;
import lombok.Getter;
import lombok.NonNull;

public class Sprite implements HasPixelBuffer {

    @Getter
    @NonNull
    private final PixelBuffer pixelBuffer;


    public Sprite(@NonNull final PixelDimension dimension) {
        pixelBuffer = new PixelBuffer(dimension);
    }

    public Sprite(
            @NonNull final PixelDimension dimension,
            @NonNull final SpriteAtlas spriteAtlas,
            final int atlasOffsetX,
            final int atlasOffsetY
    ) {
        pixelBuffer = new PixelBuffer(dimension);
        loadSpriteFromAtlas(spriteAtlas, atlasOffsetX, atlasOffsetY, false, false);
    }

    // @TODO: Put the invert options into the PixelBuffer copy function and use it here?
    // @TODO: Add Test
    private void loadSpriteFromAtlas(
            @NonNull final SpriteAtlas spriteAtlas,
            final int atlasOffsetX,
            final int atlasOffsetY,
            final boolean invertX,
            final boolean invertY
    ) {
        for (int y = 0; y < pixelBuffer.getDimension().getHeightY(); y++) {
            int pickY = y;
            if (invertY) {
                pickY = pixelBuffer.getDimension().getHeightY() - y;
            }
            for (int x = 0; x < pixelBuffer.getDimension().getWidthX(); x++) {
                int pickX = x;
                if (invertX) {
                    pickX = pixelBuffer.getDimension().getWidthX() - x;
                }

                pixelBuffer.bufferPixelAt(x, y, getPixelAtIndex(spriteAtlas, atlasOffsetX, atlasOffsetY, pickY, pickX));
            }
        }
    }

    private static int getPixelAtIndex(
            @NonNull final SpriteAtlas spriteAtlas,
            final int atlasOffsetX,
            final int atlasOffsetY,
            final int pickY,
            final int pickX
    ) {
        return spriteAtlas.getPixelBuffer().scanPixelAtIndex((pickX + atlasOffsetX) + (pickY + atlasOffsetY) * spriteAtlas.getPixelBuffer().getDimension().getWidthX());
    }

    public Sprite(
            @NonNull final PixelDimension dimension,
            @NonNull final SpriteAtlas spriteAtlas,
            final int offsetX,
            final int offsetY,
            final boolean invertX,
            final boolean invertY
    ) {
        pixelBuffer = new PixelBuffer(dimension);
        loadSpriteFromAtlas(spriteAtlas, offsetX, offsetY, invertX, invertY);
    }

}
