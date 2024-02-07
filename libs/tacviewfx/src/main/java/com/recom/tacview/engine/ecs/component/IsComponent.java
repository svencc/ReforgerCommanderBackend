package com.recom.tacview.engine.ecs.component;

import com.recom.tacview.engine.IsUpdatable;
import lombok.NonNull;

public interface IsComponent extends MaybeBelongsToEntity, HasComponentType, IsUpdatable {

    @NonNull
    ComponentType componentType();

    @NonNull
    Class<? extends ComponentTemplate> componentClass();

    int getComponentProcessingOrder();

}
