package com.recom.commons.model;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class Vector3D {

    private final double x;
    private final double y;
    private final double z;

}
