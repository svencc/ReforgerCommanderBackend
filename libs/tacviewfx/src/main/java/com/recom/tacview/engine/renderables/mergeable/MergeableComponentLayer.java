package com.recom.tacview.engine.renderables.mergeable;

import com.recom.tacview.engine.ecs.component.ComponentType;
import com.recom.tacview.engine.ecs.component.PhysicCoreComponent;
import com.recom.tacview.engine.ecs.component.RenderableComponent;
import com.recom.tacview.engine.ecs.environment.Environment;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

@Slf4j
public class MergeableComponentLayer extends BufferedMergeableTemplate implements IsMergeableComponentLayer {

    @NonNull
    private final Environment environment;
    @Getter
    @NonNull
    private final Integer renderLayer;
    @NonNull
    private final List<RenderableComponent> components;
    @NonNull
    private final ExecutorService renderExecutorService; // @TODO <<<--- das nochmal überdenken; muss das sein?


    public MergeableComponentLayer(
            @NonNull final Environment environment,
            @NonNull final Integer renderLayer,
            @NonNull final List<RenderableComponent> components
    ) {
        super(environment.getRendererProperties().toRendererDimension(), environment.getRenderProvider());
        this.environment = environment;
        this.renderLayer = renderLayer;
        this.components = components;
        this.renderExecutorService = environment.getRendererExecutorProvider().provideNewExecutor();

        for (final RenderableComponent renderableComponent : components) {
            renderableComponent.setMergeableComponentLayer(this);
        }
    }

    @Override
    public void prepareBuffer() {
        super.prepareBuffer();

        prepareComponentBufferInParallel(components);
        for (final RenderableComponent component : components) {
            int offsetX = 0;
            int offsetY = 0;

            final Optional<PhysicCoreComponent> maybePhysicCoreComponent = component.getEntity().locateComponent(ComponentType.PhysicsCoreComponent);
            if (maybePhysicCoreComponent.isPresent()) {
                offsetX = (int) maybePhysicCoreComponent.get().getPositionX();
                offsetY = (int) maybePhysicCoreComponent.get().getPositionY();
            }

            environment.getRenderProvider().provide().render(component.getPixelBuffer(), this.getPixelBuffer(), offsetX, offsetY);
        }

        // @TODO <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        // @TODO da müssen jetzt camera position und entity/physic_component_core position mit rein
        // @TODO <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    }

    private void prepareComponentBufferInParallel(@NonNull final List<RenderableComponent> renderableComponents) {
        final CountDownLatch latch = new CountDownLatch(renderableComponents.size());
        for (final RenderableComponent renderableComponent : renderableComponents) {
            renderExecutorService.execute(() -> {
                try {
                    if (renderableComponent.getPixelBuffer().isDirty()) {
                        renderableComponent.prepareBuffer();
                    }
                } catch (final Exception e) {
                    log.error("{}: {}\n{}", getClass().getName(), e.getMessage(), e.getStackTrace());
                } finally {
                    latch.countDown();
                }
            });
        }

        try {
            latch.await();
        } catch (final InterruptedException e) {
            log.error("{}: {}\n{}", getClass().getName(), e.getMessage(), e.getStackTrace());
        }
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public void propagateCleanStateToChildren() {
        setDirty(false);
        components.forEach(RenderableComponent::propagateCleanStateToChildren);
    }

    @Override
    public void propagateDirtyStateToParent() {
        setDirty(true);
        environment.getRenderPipeline().propagateDirtyStateToParent();
    }

}
