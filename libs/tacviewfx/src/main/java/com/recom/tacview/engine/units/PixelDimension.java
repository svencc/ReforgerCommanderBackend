package com.recom.tacview.engine.units;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class PixelDimension {

    /**
     * @TODO To better reuse PixelDimesions, provide a factory, which will register and lookup, if such a Dimension already exists ... (Multiton?)
     */

    private int widthX;
    private int heightY;

}
