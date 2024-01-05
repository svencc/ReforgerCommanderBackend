package com.recom.tacview.engine.entity.component.refactor;

import com.recom.tacview.engine.entity.component.ComponentTemplate;
import com.recom.tacview.engine.entity.component.ComponentType;
import com.recom.tacview.engine.renderables.HasMergeable;
import com.recom.tacview.engine.renderables.Mergeable;
import lombok.Getter;
import lombok.NonNull;

public class MergeableLayerComponent extends ComponentTemplate implements HasMergeable {

    @Getter
    @NonNull
    private final Mergeable mergeable;

    public MergeableLayerComponent(final Mergeable mergeable) {
        super(ComponentType.BASE_LAYER);
        this.mergeable = mergeable;
    }

}
