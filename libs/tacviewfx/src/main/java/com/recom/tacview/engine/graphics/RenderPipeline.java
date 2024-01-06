package com.recom.tacview.engine.graphics;

import com.recom.tacview.engine.renderables.Mergeable;
import com.recom.tacview.engine.renderables.Soilable;
import com.recom.tacview.engine.renderables.mergeable.MergeableComponentLayer;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class RenderPipeline implements Soilable {

    @Getter
    @NonNull
    private final List<MergeableComponentLayer> layers = new ArrayList<>();

    @Setter
    private boolean dirty = true;

    @Override
    public boolean isDirty() {
        if (layers.isEmpty()) {
            return false;
        } else {
            return layers.stream()
                    .map(Mergeable::isDirty)
                    .reduce(true, (first, second) -> first && second);
        }
    }

}
