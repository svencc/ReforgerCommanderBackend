package com.recom.commons.model.maprendererpipeline;

import com.recom.commons.model.maprendererpipeline.report.RendererReport;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class MapComposerWorkPackage {

    @NonNull
    private final MapComposerConfiguration mapComposerConfiguration;


    @NonNull
    private final MapRendererPipelineArtifacts artifacts;


    @NonNull
    private final RendererReport report = new RendererReport();

}
