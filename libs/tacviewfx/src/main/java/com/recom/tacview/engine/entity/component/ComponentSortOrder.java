package com.recom.tacview.engine.entity.component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ComponentSortOrder {

    PHYSIC_CORE(0),
    PHYSICS(1000),
    INPUT(2000),
    RENDER(9000);

    private final int sortOrder;

    public int sortOrder() {
        return sortOrder;
    }

}
