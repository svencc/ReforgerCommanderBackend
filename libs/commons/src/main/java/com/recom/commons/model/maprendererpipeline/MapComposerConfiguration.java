package com.recom.commons.model.maprendererpipeline;

import com.recom.commons.map.rasterizer.mapdesignscheme.MapDesignScheme;
import com.recom.commons.model.DEMDescriptor;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MapComposerConfiguration {

    @NonNull
    private DEMDescriptor demDescriptor;

    @NonNull
    private MapDesignScheme mapDesignScheme;

    @NonNull
    @Builder.Default
    private List<MapLayerRasterizerConfiguration> rendererConfiguration = new ArrayList<>();

}
