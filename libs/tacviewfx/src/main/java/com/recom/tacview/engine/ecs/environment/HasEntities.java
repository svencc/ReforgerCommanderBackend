package com.recom.tacview.engine.ecs.environment;

import com.recom.tacview.engine.ecs.entity.Entity;
import lombok.NonNull;

import java.util.List;

public interface HasEntities {

    @NonNull
    List<Entity> getEntities();

}
