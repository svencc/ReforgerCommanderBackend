package com.recom.tacview.engine.entitycomponentsystem.environment;

import com.recom.tacview.engine.entitycomponentsystem.entity.Entity;
import lombok.NonNull;

import java.util.List;

public interface HasEntities {

    @NonNull List<Entity> getEntities();

}
