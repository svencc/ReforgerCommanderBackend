package com.recom.commons.map.rasterizer.configuration;

import com.recom.commons.model.maprendererpipeline.MapComposerWorkPackage;
import lombok.NonNull;

import java.io.IOException;
import java.util.Optional;

public interface MapLayerRasterizer<ARTEFACT_TYPE> extends HasMapLayerRasterizerConfiguration {

    @NonNull
    String getRasterizerName();

    void render(@NonNull final MapComposerWorkPackage workPackage) throws IOException;

    @NonNull
    Optional<ARTEFACT_TYPE> findMyArtefactFromWorkPackage(@NonNull final MapComposerWorkPackage workPackage);

}
