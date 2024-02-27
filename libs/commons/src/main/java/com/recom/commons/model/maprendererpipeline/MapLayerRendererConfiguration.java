package com.recom.commons.model.maprendererpipeline;

import com.recom.commons.map.rasterizer.configuration.LayerOrder;
import com.recom.commons.map.rasterizer.configuration.MapLayerRenderer;
import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MapLayerRendererConfiguration {

    @Builder.Default
    private boolean enabled = true;
    @Builder.Default
    private boolean visible = true;
    @Builder.Default
    private boolean sequentialCoreData = false;
    @NonNull
    private LayerOrder layerOrder;


    public void applyConfiguration(@NonNull final MapLayerRenderer renderer) {
        renderer.getMapLayerRendererConfiguration().setLayerOrder(layerOrder);
        renderer.getMapLayerRendererConfiguration().setVisible(visible);
        renderer.getMapLayerRendererConfiguration().setEnabled(enabled);
    }

}
