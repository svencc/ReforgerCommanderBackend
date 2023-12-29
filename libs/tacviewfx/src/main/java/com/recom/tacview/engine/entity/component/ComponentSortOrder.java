package com.recom.tacview.engine.entity.component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ComponentSortOrder {

    PHYSIC_CORE(1000),
    PHYSICS(2000),
    INPUT(3000),
    CAMERA(9000),
    RENDER(9100);

    private final int sortOrder;

    public int sortOrder() {
        return sortOrder;
    }

}
