package com.recom.tacview.engine.entity.interfaces;

import com.recom.tacview.engine.entity.component.ComponentType;
import lombok.NonNull;

public interface HasComponentType {

    @NonNull
    ComponentType componentType();

}
