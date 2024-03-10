package com.recom.commons.model.maprendererpipeline.dataprovider.village;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.math.BigDecimal;

@Getter
@Builder
public class StructureItem {

    @NonNull
    private final BigDecimal coordinateX;
    @NonNull
    private final BigDecimal coordinateY;

}
