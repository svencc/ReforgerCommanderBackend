package com.recom.tacview.engine.renderables.mergeable;

import com.recom.tacview.engine.graphics.IsBufferable;
import com.recom.tacview.engine.graphics.buffer.NullPixelBuffer;
import com.recom.tacview.engine.graphics.buffer.PixelBuffer;
import lombok.NonNull;

public class NullMergeableComponentLayer implements IsMergeableComponentLayer {

    @NonNull
    public static final NullMergeableComponentLayer INSTANCE = new NullMergeableComponentLayer();

    private NullMergeableComponentLayer() {
    }

    @NonNull
    public final Integer getRenderLayer() {
        return 0;
    }


    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public void setDirty(boolean isDirty) {

    }

    @Override
    public void mergeBufferWith(@NonNull PixelBuffer targetBuffer, int offsetX, int offsetY) {

    }

    @Override
    public void mergeBufferWith(@NonNull IsBufferable targetBuffer, int offsetX, int offsetY) {

    }

    @Override
    public void prepareBuffer() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public void propagateCleanStateToChildren() {

    }

    @Override
    public void propagateDirtyStateToParent() {

    }

    @Override
    public @NonNull PixelBuffer getPixelBuffer() {
        return NullPixelBuffer.INSTANCE;
    }
}
