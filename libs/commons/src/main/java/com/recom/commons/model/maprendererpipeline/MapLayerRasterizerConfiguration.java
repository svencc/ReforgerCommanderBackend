package com.recom.commons.model.maprendererpipeline;

import com.recom.commons.map.rasterizer.configuration.MapLayerRasterizer;
import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MapLayerRasterizerConfiguration {

    @NonNull
    private String rasterizerName;
    @Builder.Default
    private boolean enabled = true;
    @Builder.Default
    private boolean visible = true;
    @Builder.Default
    private boolean sequentialCoreData = false;
    @NonNull
    private int layerOrder;

    public void applyConfiguration(@NonNull final MapLayerRasterizer renderer) {
        renderer.getMapLayerRasterizerConfiguration().setLayerOrder(layerOrder);
        renderer.getMapLayerRasterizerConfiguration().setVisible(visible);
        renderer.getMapLayerRasterizerConfiguration().setEnabled(enabled);
    }

}
