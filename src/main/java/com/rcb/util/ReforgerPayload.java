package com.rcb.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@UtilityClass
public class ReforgerPayload {

    @NonNull
    private final static ObjectMapper objectMapper = new ObjectMapper();

    @NonNull
    public <T> Optional<T> parse(
            @NonNull Map<String, String> payload,
            @NonNull Class<T> clazz
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
            log.info("Cannot parse a Reforger JSON DTO", e);
            return Optional.empty();
        }
    }

}
