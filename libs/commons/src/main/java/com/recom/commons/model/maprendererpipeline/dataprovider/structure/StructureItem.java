package com.recom.commons.model.maprendererpipeline.dataprovider.structure;

import com.recom.commons.model.maprendererpipeline.dataprovider.SpacialItem;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.math.BigDecimal;

@Getter
@Builder
public class StructureItem implements SpacialItem {

    @NonNull
    private final BigDecimal coordinateX;
    @NonNull
    private final BigDecimal coordinateY;

}
