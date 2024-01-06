package com.recom.tacview.engine.entitycomponentsystem.component;

import com.recom.tacview.engine.entitycomponentsystem.entity.IsEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public interface MaybeBelongsToEntity {

    @Nullable
    IsEntity getEntity();

    void setEntity(@NonNull final IsEntity entity);

    boolean hasNullEntity();

}
