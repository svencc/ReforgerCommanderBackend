package com.recom.tacview.engine.renderables.mergeable;

import com.recom.tacview.engine.renderables.Mergeable;
import com.recom.tacview.engine.graphics.Scanable;
import com.recom.tacview.engine.units.PixelDimension;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class ScanableMergeableTemplate implements Scanable, Mergeable {

    @Getter
    @NonNull
    protected final PixelDimension Dimension;

}
