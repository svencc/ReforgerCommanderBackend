package com.recom.tacview.engine.entity;

import com.recom.tacview.engine.entity.component.ComponentType;
import com.recom.tacview.engine.entity.component.RenderableComponent;
import com.recom.tacview.engine.entity.interfaces.IsEnvironment;
import com.recom.tacview.engine.graphics.RenderPipeline;
import com.recom.tacview.engine.renderables.mergeable.MergeableComponentLayer;
import com.recom.tacview.engine.renderer.RenderProvider;
import com.recom.tacview.property.RendererProperties;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class Environment implements IsEnvironment {

    @Getter
    @NonNull
    private List<Entity> entities = new ArrayList<>();

    //@TODO: extract this to a generic dirty cache!
    @Setter
    @Getter()
    private boolean renderableComponentListModified = true;
    private final Map<Integer, List<RenderableComponent>> renderableComponentList = new HashMap<>();
    private final RenderPipeline renderPipeline = new RenderPipeline();
    //@TODO: extract this to a generic dirty cache!

    public void registerNewComponent(@NonNull final Entity entity) {
        entities.add(entity);
    }

    public void update(final long elapsedNanoTime) {
        for (final Entity entity : entities) {
            entity.update(elapsedNanoTime);
        }
    }







    // @TODO: needs to be extended ....
    // Refactor to generic dirty cache!
    @NonNull
    public Map<Integer, List<RenderableComponent>> getRenderableComponents() {
        if (renderableComponentListModified) {
            renderableComponentList.clear();
            renderableComponentList.putAll(entities.stream()
                    .flatMap(entity -> entity.<RenderableComponent>locateComponents(ComponentType.RenderableComponent).stream())
                    .collect(Collectors.groupingBy(RenderableComponent::getZIndex))
            );
            renderableComponentListModified = false;
        }

        return renderableComponentList;
    }

    @NonNull
    public List<MergeableComponentLayer> getMergeableComponentLayer(
            @NonNull final RendererProperties rendererProperties,
            @NonNull final RenderProvider renderProvider
    ) {
        return getRenderableComponents().entrySet().stream()
                .map(entrySet -> new MergeableComponentLayer(rendererProperties, renderProvider, entrySet.getKey(), entrySet.getValue()))
                .toList();
    }

    @NonNull
    public RenderPipeline getRenderPipeline(
            @NonNull final RendererProperties rendererProperties,
            @NonNull final RenderProvider renderProvider
    ) {
        if (renderPipeline.isPipelineDirty()) {
            renderPipeline.setDirty(false);
            renderPipeline.getLayers().clear();
            renderPipeline.getLayers().addAll(getMergeableComponentLayer(rendererProperties, renderProvider));
        }
        return renderPipeline;
    }
}
