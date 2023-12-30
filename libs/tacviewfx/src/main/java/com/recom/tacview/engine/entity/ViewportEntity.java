package com.recom.tacview.engine.entity;

import com.recom.tacview.engine.entity.component.PhysicCoreComponent;

import java.util.List;

public class ViewportEntity extends Entity {

    public ViewportEntity() {
        super();
        addComponents(List.of(
                new PhysicCoreComponent()
        ));
    }


}
