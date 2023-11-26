package com.recom.tacview.game.hex.level;

import com.recom.tacview.engine.components.sprite.Sprite;
import com.recom.tacview.engine.components.tile.Tile;
import com.recom.tacview.engine.graphics.buffer.PixelBuffer;
import com.recom.tacview.engine.units.PixelCoordinate;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;

@Data
@Builder
public class HexTile implements Tile {

    @Getter
    @NonNull
    private final PixelCoordinate position;
    @Getter
    @NonNull
    private final Sprite sprite;
    private final int layoutNumeration;
    private final int q;
    private final int r;
    private final int s;
    @Getter
    @NonNull
    private final HexMap hexMap;

    @Override
    public PixelBuffer getPixelBuffer() {
        return sprite.getPixelBuffer();
    }

}
