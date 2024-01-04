package com.recom.tacview.engine.graphics;

import com.recom.tacview.engine.entity.environment.EnvironmentBase;
import com.recom.tacview.engine.renderables.HasPixelBuffer;
import lombok.NonNull;

public interface Composable extends HasPixelBuffer {

    int compose(@NonNull final EnvironmentBase environment);

}