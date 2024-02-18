package com.recom.commons.model;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
public class SlopeAndAspectMap {

    private final SlopeAndAspect[][] slopeAndAspectMap;

}