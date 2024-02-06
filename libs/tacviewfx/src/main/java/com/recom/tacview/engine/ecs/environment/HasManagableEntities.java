package com.recom.tacview.engine.ecs.environment;

import com.recom.tacview.engine.ecs.entity.Entity;
import lombok.NonNull;

import java.util.List;

public interface HasManagableEntities extends HasEntities {


    void registerNewEntity(@NonNull final Entity entity);

    void registerNewEntities(@NonNull final List<Entity> entitiesToAdd);

}
