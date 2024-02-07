package com.recom.tacview.engine.ecs.component;

import com.recom.commons.units.PixelDimension;
import com.recom.tacview.engine.ecs.ChildPropagateableSoilableState;
import com.recom.tacview.engine.ecs.ParentPropagateableSoilableState;
import com.recom.tacview.engine.graphics.buffer.NullPixelBuffer;
import com.recom.tacview.engine.graphics.buffer.PixelBuffer;
import com.recom.tacview.engine.renderables.HasPixelBuffer;
import com.recom.tacview.engine.renderables.mergeable.MergeableComponentLayer;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.Optional;

@Getter
public abstract class RenderableComponent extends ComponentTemplate implements HasPixelBuffer, BelongsToMergeableComponentLayer, ParentPropagateableSoilableState, ChildPropagateableSoilableState {

    @Setter
    private int zIndex = 0;
    @Setter
    @NonNull
    protected PixelBuffer pixelBuffer;
    @NonNull
    private Optional<MergeableComponentLayer> maybeMergeableComponentLayer = Optional.empty();

    public RenderableComponent() {
        super(ComponentType.RenderableComponent);
        this.pixelBuffer = NullPixelBuffer.INSTANCE;
    }

    public RenderableComponent(@NonNull final PixelBuffer pixelBuffer) {
        super(ComponentType.RenderableComponent);
        this.pixelBuffer = pixelBuffer;
    }

    public RenderableComponent(@NonNull final PixelDimension dimension) {
        super(ComponentType.RenderableComponent);
        this.pixelBuffer = new PixelBuffer(dimension);
    }

    public void setMergeableComponentLayer(@NonNull final MergeableComponentLayer mergeableComponentLayer) {
        this.maybeMergeableComponentLayer = Optional.of(mergeableComponentLayer);
    }

    public void propagateCleanStateToChildren() {
        pixelBuffer.setDirty(false);
    }

    public void propagateDirtyStateToParent() {
        pixelBuffer.setDirty(true);
        maybeMergeableComponentLayer.ifPresent(MergeableComponentLayer::propagateDirtyStateToParent);
    }

    public void prepareBuffer() {

    }

}
