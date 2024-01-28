package com.recom.tacview.engine.renderables.mergeable;

import com.recom.tacview.engine.graphics.IsScanable;
import com.recom.tacview.engine.renderables.IsMergeable;
import com.recom.tacview.engine.units.PixelDimension;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class ScanableMergeableTemplate implements IsScanable, IsMergeable {

    @Getter
    @NonNull
    protected final PixelDimension Dimension;


    @Override
    public boolean isDirty() {
        return true;
    }

    @Override
    public void setDirty(boolean isDirty) {
        // nothing to do here, as the scanable noise is always dirty
    }

}
