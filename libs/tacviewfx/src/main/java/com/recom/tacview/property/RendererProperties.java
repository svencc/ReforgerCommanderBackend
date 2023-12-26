package com.recom.tacview.property;

import com.recom.tacview.engine.units.PixelDimension;
import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties("engine.renderer")
public class RendererProperties {

    private static PixelDimension singletonPixelDimension = null;
    private int width;
    private int height;
    private boolean parallelizedRendering;
    private int threadPoolSize;
    private ComposerProperties composer;

    @NonNull
    public PixelDimension toRendererDimension() {
        if (singletonPixelDimension == null) {
            singletonPixelDimension = PixelDimension.builder()
                    .widthX(getWidth())
                    .heightY(getHeight())
                    .build();
        }

        return singletonPixelDimension;
    }

}
