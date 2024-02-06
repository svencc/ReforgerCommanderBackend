package com.recom.tacview.engine.graphics.renderpipeline;

import com.recom.tacview.engine.ecs.component.RenderableComponent;
import com.recom.tacview.engine.renderables.mergeable.MergeableComponentLayer;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NullRenderPipeline implements IsRenderPipeline {

    @NonNull
    public static final NullRenderPipeline INSTANCE = new NullRenderPipeline();


    private NullRenderPipeline() {
    }

    @NonNull
    public Map<Integer, List<RenderableComponent>> getRenderableComponentList = new HashMap<>();

    @NonNull
    public List<MergeableComponentLayer> getLayers() {
        return Collections.emptyList();
    }

    @Getter
    @Setter
    private boolean dirty = true;

    public void updateDirtyState() {
    }

    public void updateLayers() {
    }

    @Override
    public void propagateCleanStateToChildren() {
    }

    @Override
    public void propagateDirtyStateToParent() {
    }

}
