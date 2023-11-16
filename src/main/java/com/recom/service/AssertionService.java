package com.recom.service;

import com.recom.entity.GameMap;
import com.recom.exception.HttpNotFoundException;
import com.recom.service.map.GameMapService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AssertionService {

    @NotNull
    private final GameMapService gameMapService;

    public GameMap provideMap(@NotNull final String mapName) {
        return gameMapService.provideGameMap(mapName).orElseThrow(() -> new HttpNotFoundException(String.format("Map %s does not exist", mapName)));
    }

}
