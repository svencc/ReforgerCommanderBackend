package com.recom.tacview.engine.renderables.mergeable;

import com.recom.tacview.engine.renderer.RenderProvider;
import com.recom.tacview.property.IsEngineProperties;
import lombok.NonNull;

public class SolidColorMergeable extends BufferedMergeableTemplate {

    private final int backgroundColor;

    public SolidColorMergeable(
            @NonNull final IsEngineProperties engineProperties,
            @NonNull final RenderProvider renderProvider,
            final int backgroundColor
    ) {
        super(engineProperties.toRendererDimension(), renderProvider);
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
