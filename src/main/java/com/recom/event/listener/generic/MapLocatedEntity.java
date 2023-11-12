package com.recom.event.listener.generic;

import lombok.NonNull;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;

public interface MapLocatedEntity {

    void setMapName(@Nullable final String mapName);

    String getMapName();

    BigDecimal getCoordinateX();

    void setCoordinateX(@NonNull final BigDecimal coordinateX);

    BigDecimal getCoordinateY();

    void setCoordinateY(@NonNull final BigDecimal coordinateY);

    BigDecimal getCoordinateZ();

    void setCoordinateZ(@NonNull final BigDecimal coordinateZ);

}
