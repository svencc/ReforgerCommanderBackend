package com.recom.tacview.engine.entitycomponentsystem.entity;

import com.recom.tacview.engine.entitycomponentsystem.environment.IsEnvironment;
import org.springframework.lang.NonNull;

public interface BelongsToEnvironment {

    @NonNull
    IsEnvironment getEnvironment();

    void setEnvironment(@NonNull final IsEnvironment entity);

}
