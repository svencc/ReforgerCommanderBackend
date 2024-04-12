package com.recom.entity.map.structure;

import com.recom.commons.model.maprendererpipeline.dataprovider.SpacialItem;
import lombok.NonNull;

import java.io.Serializable;
import java.math.BigDecimal;

public interface SpacialItemProjection extends SpacialItem, Serializable {

    @NonNull
    BigDecimal getCoordinateX();

    @NonNull
    BigDecimal getCoordinateY();

}
