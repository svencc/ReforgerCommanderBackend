package com.recom.tacview.engine.renderables.mergeable;

import com.recom.tacview.engine.renderer.RenderProvider;
import com.recom.tacview.property.RendererProperties;
import lombok.NonNull;

public class SolidScreenMergeable extends BufferedMergeableTemplate {

    private final int backgroundColor;

    public SolidScreenMergeable(
            @NonNull final RendererProperties rendererProperties,
            @NonNull final RenderProvider renderProvider,
            final int backgroundColor
    ) {
        super(rendererProperties.toRendererDimension(), renderProvider);
        this.backgroundColor = backgroundColor;
        getPixelBuffer().setDirty(true);
    }

    @Override
    public void prepareBuffer() {
        if (isDirty()) {
            getPixelBuffer().fillBuffer(backgroundColor);
            getPixelBuffer().setDirty(false);
        }
    }

}
