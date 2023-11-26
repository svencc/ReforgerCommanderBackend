package com.recom.tacview.engine.graphics.renderer;

import com.recom.tacview.property.RendererProperties;
import com.recom.tacview.service.argb.ARGBCalculatorProvider;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
class SoftwareRenderer extends RendererTemplate {

    SoftwareRenderer(@NonNull final RendererProperties rendererProperties, @NonNull final ARGBCalculatorProvider argbCalculatorProvider) {
        super(rendererProperties, argbCalculatorProvider);
    }

}
