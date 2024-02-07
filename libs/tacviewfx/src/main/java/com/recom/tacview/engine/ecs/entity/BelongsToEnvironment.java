package com.recom.tacview.engine.ecs.entity;

import com.recom.tacview.engine.ecs.environment.Environment;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface BelongsToEnvironment {

    @NonNull
    Optional<Environment> getMaybeEnvironment();

    void setEnvironment(@NonNull final Environment entity);

}
