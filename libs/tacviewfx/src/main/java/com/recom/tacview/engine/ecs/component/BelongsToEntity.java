package com.recom.tacview.engine.ecs.component;

import com.recom.tacview.engine.ecs.entity.IsEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public interface BelongsToEntity {

    @Nullable
    IsEntity getEntity();

    void setEntity(@NonNull final IsEntity entity);

}
