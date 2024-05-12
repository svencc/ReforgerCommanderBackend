package com.recom.commons.map.rasterizer.batch1;

import com.recom.commons.calculator.d8algorithm.D8AlgorithmForSlopeAndAspectMap;
import com.recom.commons.map.rasterizer.configuration.BatchOrder;
import com.recom.commons.map.rasterizer.configuration.LayerOrder;
import com.recom.commons.map.rasterizer.configuration.MapLayerRasterizer;
import com.recom.commons.model.DEMDescriptor;
import com.recom.commons.model.SlopeAndAspect;
import com.recom.commons.model.SlopeAndAspectMap;
import com.recom.commons.model.maprendererpipeline.CreatedArtifact;
import com.recom.commons.model.maprendererpipeline.MapComposerWorkPackage;
import com.recom.commons.model.maprendererpipeline.MapLayerRasterizerConfiguration;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.Optional;


@Getter
@Setter
public class SlopeAndAspectMapRasterizer implements MapLayerRasterizer<SlopeAndAspectMap> {

    @Getter
    @NonNull
    private final MapLayerRasterizerConfiguration mapLayerRasterizerConfiguration = MapLayerRasterizerConfiguration.builder()
            .rasterizerName(getClass().getSimpleName())
            .batch(BatchOrder.BASIC_BATCH)
            .layerOrder(LayerOrder.SLOPE_AND_ASPECT_MAP)
            .build();

    @NonNull
    private SlopeAndAspectMap rasterizeSlopeAndAspectMap(@NonNull final DEMDescriptor demDescriptor) {
        final D8AlgorithmForSlopeAndAspectMap algorithmForSlopeAndAspect = new D8AlgorithmForSlopeAndAspectMap(demDescriptor.getStepSize().doubleValue());
        final SlopeAndAspect[][] slopeAndAspects = algorithmForSlopeAndAspect.generateSlopeAndAspectMap(demDescriptor.getDem());

        return SlopeAndAspectMap.builder()
                .slopeAndAspectMap(slopeAndAspects)
                .build();
    }

    @Override
    public String getRasterizerName() {
        return getClass().getSimpleName();
    }

    @Override
    public void render(@NonNull final MapComposerWorkPackage workPackage) {
        final SlopeAndAspectMap slopeAndAspectMap = rasterizeSlopeAndAspectMap(workPackage.getMapComposerConfiguration().getDemDescriptor());
        workPackage.getPipelineArtifacts().addArtifact(this, slopeAndAspectMap);
    }

    @NonNull
    @Override
    public Optional<SlopeAndAspectMap> findMyArtefactFromWorkPackage(@NonNull MapComposerWorkPackage workPackage) {
        return workPackage.getPipelineArtifacts().getArtifactFrom(getClass()).map(CreatedArtifact::getData);
    }

}