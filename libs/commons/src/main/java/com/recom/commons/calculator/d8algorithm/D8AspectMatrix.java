package com.recom.commons.calculator.d8algorithm;


import com.recom.commons.model.Aspect;

abstract class D8AspectMatrix {

    static final int[] directionXComponentMatrix = {-1, -1, 0, 1, 1, 1, 0, -1};
    static final int[] directionYComponentMatrix = {0, 1, 1, 1, 0, -1, -1, -1};
    static final Aspect[] aspects = Aspect.values();

}
