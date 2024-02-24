package com.recom.commons.model;

import com.recom.commons.rasterizer.mapcolorscheme.MapDesignScheme;
import lombok.Data;
import lombok.NonNull;
import org.springframework.lang.Nullable;

import java.io.ByteArrayOutputStream;

@Data
public class MapRendererPipelineArtefacts {

    @NonNull
    private MapDesignScheme mapDesignScheme;
    @NonNull
    private DEMDescriptor demDescriptor;
    @Nullable
    private SlopeAndAspectMap slopeAndAspectMap;
    @Nullable
    private ByteArrayOutputStream rasterizedHeightMap;
    @Nullable
    private ByteArrayOutputStream rasterizedShadowedMap;
    @Nullable
    private ByteArrayOutputStream rasterizedContourMap;
    @Nullable
    private ByteArrayOutputStream rasterizedSlopeMap;

}
