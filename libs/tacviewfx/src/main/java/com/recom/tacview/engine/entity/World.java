package com.recom.tacview.engine.entity;

import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

public abstract class World {

    @Getter
    @NonNull
    private List<Entity> entities = new ArrayList<>();

    public void update() {
        entities.forEach(Entity::update);
    }

}
