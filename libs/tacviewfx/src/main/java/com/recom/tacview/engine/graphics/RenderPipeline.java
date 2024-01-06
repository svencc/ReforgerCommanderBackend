package com.recom.tacview.engine.graphics;

import com.recom.tacview.engine.entitycomponentsystem.environment.Environment;
import com.recom.tacview.engine.entitycomponentsystem.component.ComponentType;
import com.recom.tacview.engine.entitycomponentsystem.component.RenderableComponent;
import com.recom.tacview.engine.renderables.Mergeable;
import com.recom.tacview.engine.renderables.Soilable;
import com.recom.tacview.engine.renderables.mergeable.MergeableComponentLayer;
import com.recom.tacview.engine.renderer.RenderProvider;
import com.recom.tacview.property.RendererProperties;
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
public class RenderPipeline implements Soilable {

    @NonNull
    private final Environment environment;

    @NonNull
    final Map<Integer, List<RenderableComponent>> renderableComponentList = new HashMap<>();

    @Getter
    @NonNull
    private final List<MergeableComponentLayer> layers = new ArrayList<>();

    @Getter
    @Setter
    private boolean dirty = true;


    private boolean updateDirtyState() {
        return layers.stream()
                .map(Mergeable::isDirty)
                .reduce(false, (first, second) -> first || second);
    }

    // @TODO: needs to be extended ....
// Refactor to generic dirty cache!
    @NonNull
    private Map<Integer, List<RenderableComponent>> getRenderableComponentsFromEnvironment() {
        if (isDirty()) {
            renderableComponentList.clear();
            renderableComponentList.putAll(environment.getEntities().stream()
                    .flatMap(entity -> entity.<RenderableComponent>locateComponents(ComponentType.RenderableComponent).stream())
                    .collect(Collectors.groupingBy(RenderableComponent::getZIndex)));
            setDirty(true);
        }

        return renderableComponentList;
    }

    @NonNull
    private List<MergeableComponentLayer> getMergeableComponentLayer(
            @NonNull final RendererProperties rendererProperties,
            @NonNull final RenderProvider renderProvider
    ) {
        return getRenderableComponentsFromEnvironment().entrySet().stream()
                .map(entrySet -> new MergeableComponentLayer(rendererProperties, renderProvider, entrySet.getKey(), entrySet.getValue()))
                .toList();
    }

    public void updateRenderPipeline() {
        if (isDirty()) {
            layers.clear();
            layers.addAll(getMergeableComponentLayer(environment.getRendererProperties(), environment.getRenderProvider()));
            setDirty(true);
        }
    }

}
