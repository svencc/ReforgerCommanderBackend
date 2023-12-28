package com.recom.tacview.engine.entity;

import lombok.Getter;

public abstract class Entity {

    @Getter
    private double x = 0.0;
    @Getter
    private double y = 0.0;

    abstract void update();

}
