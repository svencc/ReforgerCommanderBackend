package com.recom.event.listener.generic.maplocated;

import com.recom.event.listener.generic.generic.MapDto;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.List;

public interface MapLocatedDto extends MapDto {

    List<BigDecimal> getCoordinates();

    void setCoordinates(@NonNull final List<BigDecimal> coordinates);

}
