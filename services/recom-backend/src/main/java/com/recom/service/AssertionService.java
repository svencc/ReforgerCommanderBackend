package com.recom.service;

import com.recom.entity.map.GameMap;
import com.recom.exception.HttpNotFoundException;
import com.recom.service.map.GameMapService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AssertionService {

    @NotNull
    private final GameMapService gameMapService;


    @NotNull
    public GameMap provideMapOrExitWith404(@NotNull final String mapName) throws HttpNotFoundException{
        return gameMapService.provideGameMap(mapName).orElseThrow(() -> new HttpNotFoundException(String.format("Map %s does not exist", mapName)));
    }

    @NotNull
    public Optional<GameMap> provideMaybeMap(@NotNull final String mapName) {
        return gameMapService.provideGameMap(mapName);
    }

}
