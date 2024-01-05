package com.recom.tacview.engine.entity.interfaces;

import com.recom.tacview.engine.Updatable;
import com.recom.tacview.engine.entity.Entity;
import lombok.NonNull;

import java.util.List;

public interface IsEnvironment extends Updatable {

    @NonNull
    List<Entity> getEntities();

    void update(final long elapsedNanoTime);

}
