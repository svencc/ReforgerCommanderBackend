package com.recom.tacview.game.hex;

import com.recom.tacview.engine.GameTemplate;
import com.recom.tacview.engine.graphics.ScreenComposer;
import com.recom.tacview.game.hex.graphics.HexMapMergeable;
import com.recom.tacview.game.hex.level.HexMap;
import com.recom.tacview.game.hex.level.HexMapConfiguration;
import com.recom.tacview.game.hex.level.HexMapLayouter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("hexgame")
@RequiredArgsConstructor
public class HexGame extends GameTemplate {

    @NonNull
    private final HexMapLayouter hexMapLayouter;
    @NonNull
    private final ScreenComposer screenComposer;
    @NonNull
    private final HexMapMergeable hexMapMergeable;

    @Override
    public void init() {
        hexMapMergeable.setHexMap(generateExampleMap());
        screenComposer.getLayerPipeline().clear();
        screenComposer.getLayerPipeline().add(hexMapMergeable);
    }

    private HexMap generateExampleMap() {
        final HexMapConfiguration mapProperties = HexMapConfiguration.builder()
                .gridHeight(170)
                .gridWidth(150)
                .build();

        return hexMapLayouter.layout(mapProperties);
    }

    @Override
    public void startGame() {

    }

}
