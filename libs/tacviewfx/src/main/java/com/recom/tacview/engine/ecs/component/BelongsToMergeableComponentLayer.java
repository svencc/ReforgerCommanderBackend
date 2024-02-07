package com.recom.tacview.engine.ecs.component;

import com.recom.tacview.engine.renderables.mergeable.MergeableComponentLayer;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface BelongsToMergeableComponentLayer {

    @NonNull
    Optional<MergeableComponentLayer> getMaybeMergeableComponentLayer();

    void setMergeableComponentLayer(@NonNull final MergeableComponentLayer mergeableComponentLayer);

}
