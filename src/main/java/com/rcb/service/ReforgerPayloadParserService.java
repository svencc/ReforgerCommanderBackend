package com.rcb.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rcb.exception.HttpBadRequestException;
import jakarta.annotation.PostConstruct;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

@Slf4j
@Service
public class ReforgerPayloadParserService {

    @NonNull
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Validator validator;

    @PostConstruct
    public void postConstruct() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @NonNull
    public <T> T parseValidated(
            @NonNull final Map<String, String> payload,
            @NonNull final Class<T> clazz
    ) {
        try {
            log.debug("parse {}", payload.keySet().stream().findFirst().get());
            T parsed = objectMapper.readValue(
                    payload.keySet().stream().findFirst().get(),
                    clazz
            );

            validateInput(parsed);

            return parsed;
        } catch (NoSuchElementException | JsonProcessingException e) {
            log.error("Cannot parse a Reforger JSON DTO {}", String.join(";\n", payload.keySet()), e);
            throw new HttpBadRequestException(e.getMessage());
        }
    }

    <T> void validateInput(@NonNull final T input) {
        final Set<ConstraintViolation<T>> violations = validator.validate(input);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

}
