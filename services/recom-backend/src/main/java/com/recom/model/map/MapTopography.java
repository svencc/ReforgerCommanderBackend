package com.recom.model.map;

import com.recom.entity.map.SquareKilometerTopographyChunk;
import com.recom.event.listener.generic.generic.MapEntity;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.List;

@Data
@Builder
public class MapTopography {

    @NonNull
    private final MapEntity mapEntity;

    @NonNull
    private final List<SquareKilometerTopographyChunk> topographyChunks;

}
