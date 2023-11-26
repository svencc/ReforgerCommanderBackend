package com.recom.tacview.game.hex.level;

import com.recom.tacview.engine.components.sprite.Sprite;
import com.recom.tacview.engine.components.sprite.SpriteAtlas;
import com.recom.tacview.engine.units.PixelCoordinate;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@NoArgsConstructor
public class HexMapLayouter {

    @NonNull
    private Sprite hexSprite;

    @NonNull
    public HexMap layout(@NonNull HexMapConfiguration hexMapConfiguration) {
        loadSprite();

        final HexMap hexMap = HexMap.builder()
                .HexMapConfiguration(hexMapConfiguration)
                .build();
        final List<HexTile> cubicHexList = new ArrayList<>();

        int startTop = 0;
        int startLeft = 0;
        int endBottom = hexMapConfiguration.getGridHeight();
        int endRight = hexMapConfiguration.getGridWidth();
        int layoutNumeration = 1;
        for (int q = startLeft; q < endRight; q++) {
            for (int r = startTop; r < endBottom; r++) {
                int layoutOffsetX = -(15 * q);
                int layoutOffsetY = 0;
                if (isOdd(q)) {
                    layoutOffsetY = (hexSprite.getPixelBuffer().getDimension().getHeightY() / 2);
                }
                cubicHexList.add(HexTile.builder()
                        .layoutNumeration(layoutNumeration)
                        .position(PixelCoordinate.builder()
                                .withX(q * hexSprite.getPixelBuffer().getDimension().getWidthX() + layoutOffsetX)
                                .heightY(r * hexSprite.getPixelBuffer().getDimension().getHeightY() + layoutOffsetY)
                                .build())
                        .q(q)
                        .r(r)
                        .s(-q - r)
                        .hexMap(hexMap)
                        .sprite(hexSprite)
                        .build());
                layoutNumeration++;
            }
        }

        hexMap.setHexTiles(cubicHexList);

        return hexMap;
    }

    private void loadSprite() {
        try {
            final SpriteAtlas spriteAtlas = new SpriteAtlas("/assets/hex62x32alpha.png");
            hexSprite = spriteAtlas.createSprite(spriteAtlas.getPixelBuffer().getDimension(), 0, 0);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(666);
        }
    }

    private boolean isOdd(int q) {
        return ((q & 0x1) == 1);
    }

}
