package com.recom.commons.model.maprendererpipeline.dataprovider;

import lombok.NonNull;

import java.math.BigDecimal;

public interface SpacialItem {

    @NonNull
    BigDecimal getCoordinateX();

    @NonNull
    BigDecimal getCoordinateY();

}
