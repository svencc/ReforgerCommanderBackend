package com.recom.tacview.util;

import com.recom.commons.units.PixelDimension;
import com.recom.commons.units.ResizeCommand;
import com.recom.tacview.engine.TacViewer;
import com.recom.tacview.property.IsEngineProperties;
import lombok.NonNull;

public class ResizeCommandFactory {

    @NonNull
    public static ResizeCommand createResizeCommand(
            @NonNull final TacViewer tacViewer,
            @NonNull final IsEngineProperties engineProperties
    ) {
        final double scaledMaxWidthX = engineProperties.getRendererWidth() * engineProperties.getRendererScale();
        final double scaledMaxHeightY = engineProperties.getRendererHeight() * engineProperties.getRendererScale();

        return ResizeCommand.of(
                PixelDimension.of((int) scaledMaxWidthX, (int) scaledMaxHeightY),
                engineProperties.getRendererScale()
        );
    }

}
