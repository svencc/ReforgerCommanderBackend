package com.recom.tacview.engine.entity.component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ComponentType {

    VIEWPORT(0),
    PHYSIC_CORE(1000),
    PHYSICS(2000),
    INPUT(3000),
    ENVIRONMENT(4000),
    RENDER_BACKGROUND(9000),
    RENDER_FOREGROUND(9100);

    private final int sortOrder;

    public int sortOrder() {
        return sortOrder;
    }

}
