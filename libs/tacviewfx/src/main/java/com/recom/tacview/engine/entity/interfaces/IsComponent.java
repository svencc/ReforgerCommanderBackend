package com.recom.tacview.engine.entity.interfaces;

import com.recom.tacview.engine.Updatable;
import com.recom.tacview.engine.entity.component.ComponentTemplate;
import com.recom.tacview.engine.entity.component.ComponentType;
import lombok.NonNull;

public interface IsComponent extends MaybeHasEntity, HasComponentType, Updatable {

    @NonNull
    ComponentType componentType();

    @NonNull
    Class<? extends ComponentTemplate> componentClass();

    int getComponentProcessingOrder();

}
