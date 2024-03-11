package com.recom.commons.model.maprendererpipeline.dataprovider.forest;

import com.recom.commons.model.maprendererpipeline.dataprovider.SpacialItem;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.math.BigDecimal;

@Getter
@Builder
public class ForestItem implements SpacialItem {

    @NonNull
    private final BigDecimal coordinateX;
    @NonNull
    private final BigDecimal coordinateY;

}
