package com.recom.tacview.engine.entitycomponentsystem.component;

import lombok.NonNull;

public interface HasComponentType {

    @NonNull
    ComponentType componentType();

}
