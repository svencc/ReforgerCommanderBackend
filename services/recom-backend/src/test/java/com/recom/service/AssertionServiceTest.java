package com.recom.service;

import com.recom.entity.map.GameMap;
import com.recom.exception.HttpNotFoundException;
import com.recom.service.map.GameMapService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

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
        final String mapName = "existingMap";
        final GameMap gameMap = GameMap.builder().name(mapName).build();
        when(gameMapService.provideGameMap(mapName)).thenReturn(Optional.of(gameMap));

        // Act and Assert
        assertDoesNotThrow(() -> serviceUnderTest.provideMapOrExitWith404(mapName));
    }

    @Test
    public void testAssertMapExists_whenMapDoesNotExist_shouldThrowHttpNotFoundException() {
        // Arrange
        String mapName = "nonExistingMap";
        when(gameMapService.provideGameMap(mapName)).thenReturn(Optional.empty());

        // Act and Assert
        HttpNotFoundException exception = assertThrows(HttpNotFoundException.class,
                () -> serviceUnderTest.provideMapOrExitWith404(mapName));

        assertEquals("Map nonExistingMap does not exist", exception.getMessage());
    }

}