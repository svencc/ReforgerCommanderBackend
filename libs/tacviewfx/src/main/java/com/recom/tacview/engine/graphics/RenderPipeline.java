package com.recom.tacview.engine.graphics;

import com.recom.tacview.engine.renderables.Mergeable;
import com.recom.tacview.engine.renderables.mergeable.MergeableComponentLayer;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class RenderPipeline {

    @Getter
    @NonNull
    private final List<MergeableComponentLayer> layers = new ArrayList<>();

    @Getter
    @Setter
    private boolean dirty = true;

    public boolean isPipelineDirty() {
        if (layers.isEmpty()) {
            return false;
        } else {
            return layers.stream()
                    .map(Mergeable::isDirty)
                    .reduce(true, (first, second) -> first && second);
        }
    }

}
