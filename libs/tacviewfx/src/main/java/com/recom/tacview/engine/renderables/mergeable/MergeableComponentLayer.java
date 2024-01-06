package com.recom.tacview.engine.renderables.mergeable;

import com.recom.tacview.engine.entity.component.RenderableComponent;
import com.recom.tacview.engine.renderer.RenderProvider;
import com.recom.tacview.property.RendererProperties;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;

public class MergeableComponentLayer extends BufferedMergeableTemplate {

    @Getter
    @NonNull
    private final Integer renderLayer;
    @NonNull
    private final List<RenderableComponent> components;


    public MergeableComponentLayer(
            @NonNull final RendererProperties rendererProperties,
            @NonNull final RenderProvider renderProvider,
            @NonNull final Integer renderLayer,
            @NonNull final List<RenderableComponent> components
    ) {
        super(rendererProperties.toRendererDimension(), renderProvider);
        this.renderLayer = renderLayer;
        this.components = components;
    }

    @Override
    public void prepareBuffer() {
        super.prepareBuffer();
    }

    @Override
    public void dispose() {
        super.dispose();
    }

}
