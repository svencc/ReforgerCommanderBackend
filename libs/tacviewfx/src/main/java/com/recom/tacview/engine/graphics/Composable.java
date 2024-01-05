package com.recom.tacview.engine.graphics;

import com.recom.tacview.engine.entity.Environment;
import com.recom.tacview.engine.renderables.HasPixelBuffer;
import lombok.NonNull;

public interface Composable extends HasPixelBuffer {

    int compose(@NonNull final Environment environment);

}