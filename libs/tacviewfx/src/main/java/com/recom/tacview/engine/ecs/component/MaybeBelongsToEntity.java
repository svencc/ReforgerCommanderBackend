package com.recom.tacview.engine.ecs.component;

import com.recom.tacview.engine.ecs.entity.IsEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Optional;

public interface MaybeBelongsToEntity {

    @Nullable
    Optional<IsEntity> getMaybeEntity();

    void setEntity(@NonNull final IsEntity entity);

}
