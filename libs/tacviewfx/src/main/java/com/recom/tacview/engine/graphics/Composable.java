package com.recom.tacview.engine.graphics;

import com.recom.tacview.engine.renderables.HasPixelBuffer;

public interface Composable extends HasPixelBuffer {

    int compose();

}