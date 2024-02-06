package com.recom.tacview.engine.graphics.renderpipeline;

import com.recom.tacview.engine.ecs.ChildPropagateableSoilableState;
import com.recom.tacview.engine.ecs.ParentPropagateableSoilableState;
import com.recom.tacview.engine.renderables.mergeable.MergeableComponentLayer;
import lombok.NonNull;

import java.util.List;

public interface IsRenderPipeline extends ParentPropagateableSoilableState, ChildPropagateableSoilableState {


    @NonNull
    List<MergeableComponentLayer> getLayers();

    boolean isDirty();

    void setDirty(final boolean dirty);

    public void updateLayers();


}
