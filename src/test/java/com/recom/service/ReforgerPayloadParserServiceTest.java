package com.recom.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.recom.exception.HttpBadRequestException;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ReforgerPayloadParserServiceTest {

    private ReforgerPayloadParserService parserService;

    @BeforeEach
    public void setUp() {
        parserService = new ReforgerPayloadParserService();
        parserService.postConstruct();
    }

    @Test
    public void testParseValidated_whenValidPayload_shouldReturnParsedObject() throws JsonProcessingException {
        // Arrange
        Map<String, String> payload = Collections.singletonMap("{\"name\": \"John\"}", "someValue");
        TestDto expectedDto = new TestDto("John");

        // Act
        TestDto result = parserService.parseValidated(payload, TestDto.class);

        // Assert
        assertEquals(expectedDto, result);
    }

    @Test
    public void testParseValidated_whenEmptyPayload_shouldThrowHttpBadRequestException() throws JsonProcessingException {
        // Arrange
        Map<String, String> payload = Collections.emptyMap();

        // Act and Assert
        HttpBadRequestException exception = assertThrows(
                HttpBadRequestException.class,
                () -> parserService.parseValidated(payload, TestDto.class)
        );
        assertNotNull(exception.getMessage());
    }

    @Test
    public void testParseValidated_whenInvalidPayload_shouldThrowHttpBadRequestException() throws JsonProcessingException {
        // Arrange
        Map<String, String> payload = Collections.singletonMap("invalidPayload", "someValue");

        // Act and Assert
        HttpBadRequestException exception = assertThrows(
                HttpBadRequestException.class,
                () -> parserService.parseValidated(payload, TestDto.class)
        );
        assertNotNull(exception.getMessage());
    }

    @Test
    public void testValidateInput_whenValidInput_shouldNotThrowException() {
        // Arrange
        TestDto validInput = new TestDto("John");

        // Act and Assert
        assertDoesNotThrow(() -> parserService.validateInput(validInput));
    }

    @Test
    public void testValidateInput_whenInvalidInput_shouldThrowConstraintViolationException() {
        // Arrange
        TestDto invalidInput = new TestDto(null);

        // Act and Assert
        ConstraintViolationException exception = assertThrows(
                ConstraintViolationException.class,
                () -> parserService.validateInput(invalidInput)
        );
        assertNotNull(exception.getConstraintViolations());
        assertFalse(exception.getConstraintViolations().isEmpty());
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class TestDto {

        @NotBlank
        private String name;

    }

}