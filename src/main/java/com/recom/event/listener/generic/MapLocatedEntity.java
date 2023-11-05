package com.recom.event.listener.generic;

import org.springframework.lang.Nullable;

public interface MapLocatedEntity {

    void setMapName(@Nullable final String mapName);
    String getMapName();

}
