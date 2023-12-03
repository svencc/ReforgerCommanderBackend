package com.recom.tacview.game.defaultexample;

import com.recom.tacview.engine.GameTemplate;
import com.recom.tacview.engine.components.mergeable.ScanableNoiseMergeable;
import com.recom.tacview.engine.graphics.ScreenComposer;
import com.recom.tacview.engine.graphics.renderer.RenderProvider;
import com.recom.tacview.property.RendererProperties;
import com.recom.tacview.service.RandomProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

@Component
//@ConditionalOnMissingBean(GameTemplate.class)
@RequiredArgsConstructor
public class DefaultGame extends GameTemplate {

    @NonNull
    private final RendererProperties rendererProperties;
    @NonNull
    private final ScreenComposer screenComposer;
    @NonNull
    private final RandomProvider randomProvider;
    @NonNull
    private final RenderProvider renderProvider;

    public void init() {
        final ScanableNoiseMergeable scanableNoiseLayer = new ScanableNoiseMergeable(renderProvider, rendererProperties.toRendererDimension(), randomProvider);
        screenComposer.getLayerPipeline().clear();
        screenComposer.getLayerPipeline().add(scanableNoiseLayer);
    }

    public void startGame() {

    }

}