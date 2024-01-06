package com.recom.tacview.engine.entity.interfaces;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public interface MaybeHasEntity {

    @Nullable
    IsEntity getEntity();

    void setEntity(@NonNull final IsEntity entity);

    boolean hasNullEntity();

}
