package com.recom.tacview.engine.entitycomponentsystem.component;

import com.recom.tacview.engine.Updatable;
import lombok.NonNull;

public interface IsComponent extends MaybeBelongsToEntity, HasComponentType, Updatable {

    @NonNull
    ComponentType componentType();

    @NonNull
    Class<? extends ComponentTemplate> componentClass();

    int getComponentProcessingOrder();

}
