package com.recom.service;

import com.recom.exception.HttpNotFoundException;
import com.recom.service.map.MapMetaDataService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AssertionService {

    @NotNull
    private final MapMetaDataService mapMetaDataService;

    public void assertMapExists(@NotNull final String mapName) {
        if (!mapMetaDataService.mapExists(mapName)) {
            throw new HttpNotFoundException(String.format("Map %s does not exist", mapName));
        }
    }

}
