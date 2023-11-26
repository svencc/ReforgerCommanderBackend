package com.recom.tacview.game.hex.level;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HexMapConfiguration {

    @Builder.Default
    private int gridHeight = 17;

    @Builder.Default
    private int gridWidth = 15;

}
