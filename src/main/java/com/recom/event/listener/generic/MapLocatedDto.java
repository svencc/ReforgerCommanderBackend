package com.recom.event.listener.generic;

import lombok.NonNull;

import java.math.BigDecimal;
import java.util.List;

public interface MapLocatedDto {

    List<BigDecimal> getCoordinates();

    void setCoordinates(@NonNull final List<BigDecimal> coordinates);

}
