package com.recom.tacview.engine.graphics;

import com.recom.tacview.engine.ecs.environment.Environment;
import com.recom.tacview.engine.renderables.HasPixelBuffer;
import lombok.NonNull;

public interface IsComposable extends HasPixelBuffer {

    void compose(@NonNull final Environment environment);

}