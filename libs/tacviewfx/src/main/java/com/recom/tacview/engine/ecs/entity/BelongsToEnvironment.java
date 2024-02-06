package com.recom.tacview.engine.ecs.entity;

import com.recom.tacview.engine.ecs.environment.IsEnvironment;
import org.springframework.lang.NonNull;

public interface BelongsToEnvironment {

    @NonNull
    IsEnvironment getEnvironment();

    void setEnvironment(@NonNull final IsEnvironment entity);

}
