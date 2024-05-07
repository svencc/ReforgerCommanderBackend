package com.recom.commons.model.maprendererpipeline;

import com.recom.commons.map.rasterizer.configuration.MapLayerRasterizer;
import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MapLayerRasterizerConfiguration {

    @Builder.Default
    private String rasterizerName = "unknown";
    @Builder.Default
    private boolean enabled = true;
    @Builder.Default
    private boolean visible = true;
    @Builder.Default
    private int layerOrder = 0;
    @Builder.Default
    private int batch = 0;

    public void applyTo(@NonNull final MapLayerRasterizer renderer) {
        renderer.getMapLayerRasterizerConfiguration().setLayerOrder(layerOrder);
        renderer.getMapLayerRasterizerConfiguration().setVisible(visible);
        renderer.getMapLayerRasterizerConfiguration().setEnabled(enabled);
    }

}
