package com.recom.commons.map.rasterizer;

import com.recom.commons.calculator.d8algorithm.D8AlgorithmForSlopeAndAspectMap;
import com.recom.commons.map.rasterizer.configuration.LayerOrder;
import com.recom.commons.map.rasterizer.configuration.MapLayerRenderer;
import com.recom.commons.model.DEMDescriptor;
import com.recom.commons.model.SlopeAndAspect;
import com.recom.commons.model.SlopeAndAspectMap;
import com.recom.commons.model.maprendererpipeline.MapComposerWorkPackage;
import com.recom.commons.model.maprendererpipeline.MapLayerRendererConfiguration;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class SlopeAndAspectMapRasterizer implements MapLayerRenderer {

    @Getter
    @NonNull
    private MapLayerRendererConfiguration mapLayerRendererConfiguration = MapLayerRendererConfiguration.builder()
            .layerOrder(LayerOrder.SLOPE_AND_ASPECT_MAP)
            .sequentialCoreData(true)
            .build();


    @NonNull
    public SlopeAndAspectMap rasterizeSlopeAndAspectMap(@NonNull final DEMDescriptor demDescriptor) {
        final D8AlgorithmForSlopeAndAspectMap algorithmForSlopeAndAspect = new D8AlgorithmForSlopeAndAspectMap(demDescriptor.getStepSize());
        final SlopeAndAspect[][] slopeAndAspects = algorithmForSlopeAndAspect.generateSlopeAndAspectMap(demDescriptor.getDem());

        return SlopeAndAspectMap.builder()
                .slopeAndAspectMap(slopeAndAspects)
                .build();
    }

    @Override
    public void render(@NonNull final MapComposerWorkPackage workPackage) {
        final SlopeAndAspectMap slopeAndAspectMap = rasterizeSlopeAndAspectMap(workPackage.getMapComposerConfiguration().getDemDescriptor());
        workPackage.getPipelineArtifacts().addArtifact(this, slopeAndAspectMap);
    }

}