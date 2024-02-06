package com.recom.tacview.engine.ecs.component;

import lombok.NonNull;

public interface HasComponentType {

    @NonNull
    ComponentType componentType();

}
