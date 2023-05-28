package com.rcb.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.rcb.dto.mapScanner.MapScannerEntityDto;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@UtilityClass
public class ReforgerPayload {

    @NonNull
    private final static ObjectMapper objectMapper = new ObjectMapper();

    @NonNull
    public <T> Optional<T> parse(
            @NonNull final Map<String, String> payload,
            @NonNull final Class<T> clazz
    ) {
        try {
            log.info("parse {}", payload.keySet().stream().findFirst().get());
            return Optional.of(
                    objectMapper.readValue(
                            payload.keySet().stream().findFirst().get(),
                            clazz
                    )
            );
        } catch (NoSuchElementException | JsonProcessingException e) {
            log.error("Cannot parse a Reforger JSON DTO {}", String.join(";\n", payload.keySet()), e);
            return Optional.empty();
        }
    }

    @NonNull
    public <T> Optional<List<T>> parseList(
            @NonNull final Map<String, String> payload,
            @NonNull final Class<T> clazz
    ) {
        try {
            log.debug("parse {} ...", payload.keySet().stream().findFirst().get());
            return Optional.of(
                    objectMapper.readValue(
                            payload.keySet().stream().findFirst().get(),
                            objectMapper.getTypeFactory().constructCollectionType(List.class, clazz)
                    )
            );
        } catch (NoSuchElementException | JsonProcessingException e) {
            log.error("Cannot parse a Reforger JSON DTO List {}", String.join(";\n", payload.keySet()), e);
            return Optional.empty();
        }
    }

}
