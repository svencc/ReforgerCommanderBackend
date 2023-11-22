package com.recom.event.listener.generic.generic;

import com.recom.entity.GameMap;
import org.springframework.lang.Nullable;

public interface MapEntity {

    void setGameMap(@Nullable final GameMap gameMap);

    GameMap getGameMap();

}
