package com.recom.commander.configuration;

import com.recom.commander.enginemodule.entity.component.RECOMMapComponent;
import com.recom.commander.enginemodule.entity.component.RECOMMapEntity;
import com.recom.commander.service.map.overview.data.MapsOverviewService;
import com.recom.commander.service.map.topography.data.MapTopographyDataService;
import com.recom.rendertools.rasterizer.HeightmapRasterizer;
import com.recom.tacview.property.RendererProperties;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
public class RECOMMapEntityConfiguration {

    @Bean()
    public RECOMMapEntity getRECOMMapEntity(
            @NonNull final RendererProperties rendererProperties, // dynamic properties in die map stecken; map-component greift dann auf entity renderer properties zu ...
            @NonNull final MapsOverviewService mapsOverviewService,
            @NonNull final MapTopographyDataService mapTopographyDataService,
            @NonNull final HeightmapRasterizer heightmapRasterizer
    ) {
        final RECOMMapEntity recomMapEntity = new RECOMMapEntity();
        final RECOMMapComponent recomMapComponent = new RECOMMapComponent(mapsOverviewService, mapTopographyDataService, heightmapRasterizer);
        recomMapEntity.addComponent(recomMapComponent);

        return recomMapEntity;
    }

}