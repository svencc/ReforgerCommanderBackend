package com.recom.tacview.util;

import com.recom.commons.units.PixelDimension;
import com.recom.commons.units.ResizeCommand;
import com.recom.tacview.property.IsEngineProperties;
import lombok.NonNull;

public class ResizeCommandFactory {

    @NonNull
    public static ResizeCommand createResizeCommand(@NonNull final IsEngineProperties engineProperties) {
        return ResizeCommand.of(
                PixelDimension.of(engineProperties.getRendererWidth(), engineProperties.getRendererHeight()),
                engineProperties.getRendererScale()
        );
    }

}
