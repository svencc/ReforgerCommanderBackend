package com.recom.service.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class StaticObjectMapperProviderTest {

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private StaticObjectMapperProvider serviceUnderTest;

    @BeforeEach
    public void setUp() {
        serviceUnderTest.postConstruct();
    }

    @Test
    public void testProvide_shouldReturnSameObjectMapperInstance() {
        // Act
        ObjectMapper result = StaticObjectMapperProvider.provide();

        // Assert
        assertNotNull(result);
        assertEquals(objectMapper, result);
    }

}