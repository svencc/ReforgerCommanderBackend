package com.recom.tacview.engine.entity.component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ComponentType {

    // INPUT Components
    INPUT(0),

    // UPDATE Components
    VIEWPORT(1000),
    PHYSIC_CORE(2000),
    PHYSICS(3000),

    // RENDERING Components
    LAYER(4000),
    ENVIRONMENT(4100),
    RENDER_BACKGROUND(9000),
    RENDER_FOREGROUND(9100);


    private final int sortOrder;

    /**
     * @return the order of execution of the component (from lower to higher)
     */
    public int sortOrder() {
        return sortOrder;
    }

}
