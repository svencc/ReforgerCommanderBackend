package com.recom.service;

import com.recom.exception.HttpNotFoundException;
import com.recom.service.map.GameMapService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssertionServiceTest {

    @Mock
    private GameMapService gameMapService;
    @InjectMocks
    private AssertionService serviceUnderTest;

    @Test
    public void testAssertMapExists_whenMapExists_shouldNotThrowException() {
        // Arrange
        String mapName = "existingMap";
        when(gameMapService.provideGameMap(mapName)).thenReturn(true);

        // Act and Assert
        assertDoesNotThrow(() -> serviceUnderTest.provideMap(mapName));
    }

    @Test
    public void testAssertMapExists_whenMapDoesNotExist_shouldThrowHttpNotFoundException() {
        // Arrange
        String mapName = "nonExistingMap";
        when(gameMapService.provideGameMap(mapName)).thenReturn(false);

        // Act and Assert
        HttpNotFoundException exception = assertThrows(HttpNotFoundException.class,
                () -> serviceUnderTest.provideMap(mapName));

        assertEquals("Map nonExistingMap does not exist", exception.getMessage());
    }

}