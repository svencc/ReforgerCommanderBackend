package com.recom.tacview.engine.renderer;

import com.recom.tacview.service.argb.ARGBCalculatorProvider;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
class SoftwareRenderer extends RendererTemplate {

    SoftwareRenderer(@NonNull final ARGBCalculatorProvider argbCalculatorProvider) {
        super(argbCalculatorProvider);
    }

}
