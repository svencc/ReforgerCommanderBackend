package com.recom.tacview.engine.entitycomponentsystem.entity;

import com.recom.tacview.engine.entitycomponentsystem.component.IsComponent;
import lombok.NonNull;

import java.util.List;

public interface HasComponents {

    @NonNull List<IsComponent> getComponents();

}
