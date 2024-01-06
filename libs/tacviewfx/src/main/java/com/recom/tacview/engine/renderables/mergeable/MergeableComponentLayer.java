package com.recom.tacview.engine.renderables.mergeable;

import com.recom.tacview.engine.entitycomponentsystem.component.RenderableComponent;
import com.recom.tacview.engine.entitycomponentsystem.environment.IsEnvironment;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;

public class MergeableComponentLayer extends BufferedMergeableTemplate implements IsMergeableComponentLayer {

    @Getter
    @NonNull
    private final Integer renderLayer;
    @NonNull
    private final List<RenderableComponent> components;
    @NonNull
    private final IsEnvironment environment;


    public MergeableComponentLayer(
            @NonNull final IsEnvironment environment,
            @NonNull final Integer renderLayer,
            @NonNull final List<RenderableComponent> components
    ) {
        super(environment.getRendererProperties().toRendererDimension(), environment.getRenderProvider());
        this.environment = environment;
        this.renderLayer = renderLayer;
        this.components = components;

        for (final RenderableComponent renderableComponent : components) {
            renderableComponent.setMergeableComponentLayer(this);
        }
    }

    @Override
    public void prepareBuffer() {
        super.prepareBuffer();
        components.forEach(RenderableComponent::prepareBuffer);
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public void propagateCleanStateToChildren() {
        components.forEach(RenderableComponent::propagateCleanStateToChildren);
        setDirty(false);

    }

    @Override
    public void propagateDirtyStateToParent() {
        setDirty(true);
        environment.getRenderPipeline().propagateDirtyStateToParent();
    }

}
