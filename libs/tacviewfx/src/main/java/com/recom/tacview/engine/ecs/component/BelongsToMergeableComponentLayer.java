package com.recom.tacview.engine.ecs.component;

import com.recom.tacview.engine.renderables.mergeable.IsMergeableComponentLayer;
import org.springframework.lang.NonNull;

public interface BelongsToMergeableComponentLayer {

    @NonNull
    IsMergeableComponentLayer getMergeableComponentLayer();

    void setMergeableComponentLayer(@NonNull final IsMergeableComponentLayer mergeableComponentLayer);

}