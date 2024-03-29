package com.recom.event.listener.generic.maplocated;

import com.recom.entity.map.SquareKilometerStructureChunk;
import com.recom.event.listener.generic.generic.MapEntity;
import lombok.NonNull;

import java.math.BigDecimal;

public interface MapLocatedEntity extends MapEntity {

    BigDecimal getCoordinateX();

    void setCoordinateX(@NonNull final BigDecimal coordinateX);

    BigDecimal getCoordinateY();

    void setCoordinateY(@NonNull final BigDecimal coordinateY);

    BigDecimal getCoordinateZ();

    void setCoordinateZ(@NonNull final BigDecimal coordinateZ);

    SquareKilometerStructureChunk getSquareKilometerStructureChunk();

    void setSquareKilometerStructureChunk(@NonNull final SquareKilometerStructureChunk squareKilometerStructureChunk);

}
