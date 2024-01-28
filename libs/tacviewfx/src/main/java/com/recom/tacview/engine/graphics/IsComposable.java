package com.recom.tacview.engine.graphics;

import com.recom.tacview.engine.entitycomponentsystem.environment.Environment;
import com.recom.tacview.engine.renderables.HasPixelBuffer;
import lombok.NonNull;

public interface IsComposable extends HasPixelBuffer {

    int compose(@NonNull final Environment environment);

}