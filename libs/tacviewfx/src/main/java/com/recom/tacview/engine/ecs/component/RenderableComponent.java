package com.recom.tacview.engine.ecs.component;

import com.recom.tacview.engine.ecs.ChildPropagateableSoilableState;
import com.recom.tacview.engine.ecs.ParentPropagateableSoilableState;
import com.recom.tacview.engine.graphics.buffer.NullPixelBuffer;
import com.recom.tacview.engine.graphics.buffer.PixelBuffer;
import com.recom.tacview.engine.renderables.HasPixelBuffer;
import com.recom.tacview.engine.renderables.mergeable.IsMergeableComponentLayer;
import com.recom.tacview.engine.renderables.mergeable.NullMergeableComponentLayer;
import com.recom.commons.units.PixelDimension;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Setter
@Getter
public abstract class RenderableComponent extends ComponentTemplate implements HasPixelBuffer, BelongsToMergeableComponentLayer, ParentPropagateableSoilableState, ChildPropagateableSoilableState {

    private int zIndex = 0;
    @NonNull
    protected PixelBuffer pixelBuffer;


    @Setter
    @NonNull
    private IsMergeableComponentLayer mergeableComponentLayer = NullMergeableComponentLayer.INSTANCE;

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

    public void propagateCleanStateToChildren() {
        pixelBuffer.setDirty(false);
    }

    public void propagateDirtyStateToParent() {
        pixelBuffer.setDirty(true);
        getMergeableComponentLayer().propagateDirtyStateToParent();
    }

    public void prepareBuffer() {

    }

}
