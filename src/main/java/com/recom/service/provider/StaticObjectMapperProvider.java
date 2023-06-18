package com.recom.service.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StaticObjectMapperProvider {

    private static ObjectMapper staticObjectMapperSingletonInstance;
    @NonNull
    private final ObjectMapper objectMapper;

    @NonNull
    public static ObjectMapper provide() {
        return staticObjectMapperSingletonInstance;
    }

    @PostConstruct
    public void postConstruct() {
        staticObjectMapperSingletonInstance = objectMapper;
    }

}
