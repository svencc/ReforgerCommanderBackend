package com.recom.tacview.engine.entity.environment;

import com.recom.tacview.engine.entity.Entity;
import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

public abstract class EnvironmentBase implements Environmental {

    @Getter
    @NonNull
    private List<Entity> entities = new ArrayList<>();

    public void update(final long elapsedNanoTime) {
        for (final Entity entity : entities) {
            entity.update(elapsedNanoTime);
        }
    }

}
