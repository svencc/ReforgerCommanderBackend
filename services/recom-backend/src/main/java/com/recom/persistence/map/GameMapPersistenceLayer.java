package com.recom.persistence.map;

import com.recom.entity.GameMap;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GameMapPersistenceLayer {

    @NonNull
    private final GameMapRepository gameMapRepository;

    @NonNull
    public List<GameMap> saveAll(@NonNull List<GameMap> distinctEntities) {
        return gameMapRepository.saveAll(distinctEntities);
    }

    @NonNull
    public GameMap save(@NonNull GameMap gameMap) {
        return gameMapRepository.save(gameMap);
    }

    @NonNull
    public Optional<GameMap> findByName(@NonNull final String mapName) {
        return gameMapRepository.findByName(mapName);
    }

    @NonNull
    public List<GameMap> findAllMapMeta() {
        return gameMapRepository.findAll();
    }

    @NonNull
    public List<String> findAllMapNames() {
        return gameMapRepository.findAll().stream()
                .map(GameMap::getName)
                .toList();
    }

    @NonNull
    public Integer delete(@NonNull final String mapName) {
        return gameMapRepository.deleteByName(mapName);
    }

}