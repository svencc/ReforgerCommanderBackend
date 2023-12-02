package com.recom.commander.property;

import com.recom.tacview.engine.units.PixelDimension;
import com.recom.tacview.property.ComposerProperties;
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
    private int scale;
    private boolean parallelizedRendering;
    private int threadPoolSize;
    private ComposerProperties composer;

    public int getScaledWidth() {
        return width;
    }

    public int getScaledHeight() {
        return height;
    }

    public PixelDimension toRendererDimension() {
        if (singletonPixelDimension == null) {
            singletonPixelDimension = PixelDimension.builder()
                    .widthX(getWidth())
                    .heightY(getHeight())
                    .build();
        }

        return singletonPixelDimension;
    }

    public int getWidth() {
        return width / scale;
    }

    public int getHeight() {
        return height / scale;
    }

}
