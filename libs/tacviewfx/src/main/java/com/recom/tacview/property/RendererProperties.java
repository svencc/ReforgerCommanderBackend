package com.recom.tacview.property;

import com.recom.tacview.engine.units.PixelDimension;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties("renderer")
public class RendererProperties {

    private static PixelDimension singletonPixelDimension = null;
    private int width;
    private int height;
    private boolean parallelizedRendering;
    private int threadPoolSize;
    private ComposerProperties composer;

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
