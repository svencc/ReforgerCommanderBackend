package com.recom.tacview.engine.entity.environment;

import com.recom.tacview.engine.entity.Entity;
import lombok.NonNull;

import java.util.List;

public interface Environmental {

    @NonNull
    List<Entity> getEntities();

    void update(final long elapsedNanoTime);

}
