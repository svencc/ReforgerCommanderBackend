package com.recom.tacview.engine.ecs.entity;

import com.recom.tacview.engine.ecs.component.IsComponent;
import lombok.NonNull;

import java.util.List;

public interface HasComponents {

    @NonNull
    List<IsComponent> getComponents();

}
