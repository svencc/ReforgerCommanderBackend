package com.recom.commons.calculator.d8algorithm;


import com.recom.commons.model.Aspect;

public abstract class D8AspectMatrix {

    public static final int[] directionXComponentMatrix = {-1, -1, 0, 1, 1, 1, 0, -1};
    public static final int[] directionYComponentMatrix = {0, 1, 1, 1, 0, -1, -1, -1};
    public static final Aspect[] aspects = Aspect.values();


}
