package com.recom.tacview.engine.entitycomponentsystem.component;

import com.recom.tacview.engine.IsUpdatable;
import lombok.NonNull;

public interface IsComponent extends BelongsToEntity, HasComponentType, IsUpdatable {

    @NonNull
    ComponentType componentType();

    @NonNull
    Class<? extends ComponentTemplate> componentClass();

    int getComponentProcessingOrder();

}
