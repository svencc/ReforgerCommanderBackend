package com.recom.commander.enginemodule;

import com.recom.tacview.engine.renderer.RenderProvider;
import com.recom.tacview.engine.components.HasPixelBuffer;
import com.recom.tacview.engine.components.Mergeable;
import com.recom.tacview.engine.components.Soilable;
import com.recom.tacview.engine.graphics.Bufferable;
import com.recom.tacview.engine.graphics.buffer.PixelBuffer;
import com.recom.tacview.property.RendererProperties;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
public class TestScreenMergeable implements Mergeable, HasPixelBuffer, Soilable {

    @NonNull
    private final RenderProvider renderProvider;
    @Getter
    @NonNull
    private final PixelBuffer pixelBuffer;
    //@Setter
    //@NonNull
    //private Optional<HexMap> hexMap = Optional.empty();
    @Setter
    @Getter
    private boolean dirty = true;

    //@Deprecated
    //@Nullable
    //private Sprite hexSprite;
    @Deprecated
    private boolean isSpriteInitialized = false;

    public TestScreenMergeable(
            @NonNull final RendererProperties rendererProperties,
            @NonNull final RenderProvider renderProvider
    ) {
        this.renderProvider = renderProvider;
        this.pixelBuffer = new PixelBuffer(rendererProperties.toRendererDimension());
    }

    //public void setHexMap(@NonNull final HexMap newHexMap) {
    //hexMap = Optional.of(newHexMap);
    //  dirty = true;
    //}

    @Override
    public void mergeBufferWith(
            @NonNull PixelBuffer targetBuffer,
            int offsetX, int offsetY
    ) {
        renderProvider.provide().render(pixelBuffer, targetBuffer, offsetY, offsetY);
    }

    @Override
    public void mergeBufferWith(
            @NonNull Bufferable targetBuffer,
            int offsetX,
            int offsetY
    ) {
        renderProvider.provide().render(pixelBuffer, targetBuffer, offsetY, offsetY);
    }

    @Override
    public void prepareBuffer() {
        if (!isSpriteInitialized) {
            initializeSprite();
            isSpriteInitialized = true;
        }

        if (dirty) {
            preRenderMapToBuffer();
            setDirty(false);
        }
    }

    @Deprecated
    private void initializeSprite() {
        /*
        try {
            final SpriteAtlas spriteAtlas = new SpriteAtlas("/assets/hex62x32alpha.png");
            hexSprite = spriteAtlas.createSprite(spriteAtlas.getPixelBuffer().getDimension(), 0, 0);
            pixelBuffer.fillBuffer(0xFF8a6f30);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(666);
        }
         */
    }

    private void preRenderMapToBuffer() {
        /*
        if (hexMap.isPresent()) {
            // calculate mapSize => set Buffer size!!!
            hexMap.get().getHexTiles().forEach(hexTile -> {
                renderProvider.provide().render(hexTile.getPixelBuffer(), pixelBuffer, hexTile.getPosition().getWithX(), hexTile.getPosition().getHeightY());
            });

        } else {
            // Default Map
            for (int y = 0; y <= Math.ceil(pixelBuffer.getDimension().getHeightY() / hexSprite.getPixelBuffer().getDimension().getHeightY()); y++) {
                for (int x = 0; x <= Math.ceil(pixelBuffer.getDimension().getWidthX() / hexSprite.getPixelBuffer().getDimension().getWidthX()); x++) {
                    int offsetX;
                    int offsetY;
                    if (isOdd(x)) {
                        offsetX = x * hexSprite.getPixelBuffer().getDimension().getWidthX() - (15 * x);
                        offsetY = y * hexSprite.getPixelBuffer().getDimension().getHeightY() - 16;
                    } else {
                        offsetX = x * hexSprite.getPixelBuffer().getDimension().getWidthX() - (15 * x);
                        offsetY = y * hexSprite.getPixelBuffer().getDimension().getHeightY();
                    }
                    renderProvider.provide().render(hexSprite.getPixelBuffer(), pixelBuffer, offsetX, offsetY);
                }
            }
        }
        */
    }

    @Override
    public void disposeBuffer() {

    }

    private boolean isOdd(int number) {
        return ((number & 0x1) == 1);
    }

}
