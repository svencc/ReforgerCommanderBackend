package com.recom.tacview.engine.entity;

import com.recom.tacview.engine.entity.interfaces.IsEnvironment;
import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

public abstract class Environment implements IsEnvironment {

    @Getter
    @NonNull
    private List<Entity> entities = new ArrayList<>();

    public void registerNewComponent(@NonNull final Entity entity) {
        entities.add(entity);
    }

    public void update(final long elapsedNanoTime) {
        for (final Entity entity : entities) {
            entity.update(elapsedNanoTime);
        }
    }

}
