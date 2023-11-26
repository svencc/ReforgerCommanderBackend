package com.recom.tacview.game.hex.level;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class HexMap {

    @NonNull
    private HexMapConfiguration HexMapConfiguration;

    @NonNull
    @Builder.Default
    private List<HexTile> hexTiles = new ArrayList<>();


}
