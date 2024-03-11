package com.recom.entity.map.structure;

import com.recom.commons.model.maprendererpipeline.dataprovider.SpacialItem;
import lombok.NonNull;

import java.math.BigDecimal;

public interface SpacialItemProjection extends SpacialItem {

    @NonNull
    BigDecimal getCoordinateX();

    @NonNull
    BigDecimal getCoordinateY();

}
