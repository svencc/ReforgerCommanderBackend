package com.recom.commons.model.maprendererpipeline;

import com.recom.commons.model.maprendererpipeline.report.RendererReport;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
@AllArgsConstructor
public class MapComposerWorkPackage {

    @NonNull
    private final MapComposerConfiguration mapComposerConfiguration;


    @NonNull
    private final MapRendererPipelineArtifacts pipelineArtifacts;


    @NonNull
    private final RendererReport report = new RendererReport();

}
