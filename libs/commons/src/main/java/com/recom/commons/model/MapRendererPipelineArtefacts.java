package com.recom.commons.model;

import com.recom.commons.rasterizer.mapdesignscheme.MapDesignScheme;
import com.recom.commons.rasterizer.meta.MapLayerPipelineRenderer;
import lombok.Data;
import lombok.NonNull;
import org.springframework.lang.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Data
public class MapRendererPipelineArtefacts {

    @NonNull
    private DEMDescriptor demDescriptor;
    @NonNull
    private MapDesignScheme mapDesignScheme;
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

    @NonNull
    private final Map<Class<? extends MapLayerPipelineRenderer>, IOException> occurredExceptionsDuringPipelineExecution = new HashMap<>();

}
