package com.recom.tacview.engine.renderables.sprite;

import com.recom.tacview.engine.graphics.buffer.PixelBuffer;
import com.recom.tacview.engine.renderables.HasPixelBuffer;
import com.recom.tacview.engine.units.PixelDimension;
import lombok.Getter;
import lombok.NonNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class SpriteAtlas implements HasPixelBuffer {

    @Getter
    @NonNull
    private final PixelBuffer pixelBuffer;

    public SpriteAtlas(@NonNull final String path) throws IOException {
        final BufferedImage image = ImageIO.read(SpriteAtlas.class.getResource(path));
        final PixelDimension dimension = PixelDimension.of(image.getWidth(), image.getHeight());

        int[] preparedBuffer = new int[dimension.getWidthX() * dimension.getHeightY()];
        image.getRGB(0, 0, dimension.getWidthX(), dimension.getHeightY(), preparedBuffer, 0, dimension.getWidthX());

        pixelBuffer = new PixelBuffer(dimension, preparedBuffer);
    }

    public Sprite createSprite(
            @NonNull final PixelDimension pixelDimension,
            final int atlasOffsetX,
            final int atlasOffsetY
    ) {
        final PixelDimension dimension = PixelDimension.of(pixelDimension.getWidthX(), pixelDimension.getHeightY());
        return new Sprite(dimension, this, atlasOffsetX, atlasOffsetY);
    }

    public Sprite createSprite(
            @NonNull final PixelDimension pixelDimension,
            final int atlasOffsetX,
            final int atlasOffsetY,
            final boolean invertX,
            final boolean invertY
    ) {
        final PixelDimension dimension = PixelDimension.of(pixelDimension.getWidthX(), pixelDimension.getHeightY());
        return new Sprite(dimension, this, atlasOffsetX, atlasOffsetY, invertX, invertY);
    }

}
