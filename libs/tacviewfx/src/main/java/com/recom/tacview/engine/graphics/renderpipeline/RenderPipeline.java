package com.recom.tacview.engine.graphics.renderpipeline;

import com.recom.tacview.engine.ecs.component.ComponentType;
import com.recom.tacview.engine.ecs.component.RenderableComponent;
import com.recom.tacview.engine.ecs.environment.Environment;
import com.recom.tacview.engine.renderables.mergeable.MergeableComponentLayer;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class RenderPipeline implements IsRenderPipeline {

    @NonNull
    private final Environment environment;
    @NonNull
    private final Map<Integer, List<RenderableComponent>> renderableComponentList = new HashMap<>();
    @Getter
    @NonNull
    private final List<MergeableComponentLayer> layers = new ArrayList<>();
    @Getter
    @Setter
    private boolean dirty = true;


    public void updateLayers() {
        layers.addAll(createMergeableComponentLayers());
        setDirty(true);
    }

    @NonNull
    private List<MergeableComponentLayer> createMergeableComponentLayers() {
        return provideRenderableComponentsFromRegisteredEntities().entrySet().stream()
                .map(entrySet -> new MergeableComponentLayer(environment, entrySet.getKey(), entrySet.getValue()))
                .toList();
    }

    @NonNull
    private Map<Integer, List<RenderableComponent>> provideRenderableComponentsFromRegisteredEntities() {
        if (isDirty()) {
            renderableComponentList.clear();
            renderableComponentList.putAll(environment.getEntities().stream()
                    .flatMap(entity -> entity.<RenderableComponent>locateComponents(ComponentType.RenderableComponent).stream())
                    .collect(Collectors.groupingBy(RenderableComponent::getZIndex))
            );
            setDirty(true);
        }

        return renderableComponentList;
    }

    @Override
    public void propagateCleanStateToChildren() {
        setDirty(false);
        layers.forEach(MergeableComponentLayer::propagateCleanStateToChildren);
    }

    @Override
    public void propagateDirtyStateToParent() {
        // this is root node, propagation ends here
        setDirty(true);
    }

}
