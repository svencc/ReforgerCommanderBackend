package com.recom.service.map.topography;

import com.recom.entity.map.GameMap;
import com.recom.entity.map.MapTopography;
import com.recom.persistence.map.topography.MapLocatedTopographyPersistenceLayer;
import com.recom.service.SerializationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MapServiceTest {

    @Mock
    private MapLocatedTopographyPersistenceLayer mapTopographyPersistenceLayer;
    @Mock
    private MapGeneratorService mapGeneratorService;
    @Mock
    private SerializationService serializationService;
    @InjectMocks
    private MapService serviceUnderTest;


    @Test
    void provideTopographyMap() throws IOException {
        // Arrange
        final GameMap gameMap = GameMap.builder().name("test").build();

        final MapTopography mapTopography = MapTopography.builder()
                .gameMap(GameMap.builder().name("test").build())
                .build();
        when(mapTopographyPersistenceLayer.findByGameMap(eq(gameMap))).thenReturn(Optional.of(mapTopography));

        final ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        final ByteArrayOutputStream byteOutputStream2 = new ByteArrayOutputStream();
        final ByteArrayOutputStream byteOutputStream3 = new ByteArrayOutputStream();
        final ByteArrayOutputStream byteOutputStream4 = new ByteArrayOutputStream();
        final byte[] bytes = {1, 2, 3};
        byteOutputStream.write(bytes);
        byteOutputStream2.write(bytes);
        byteOutputStream3.write(bytes);
        byteOutputStream4.write(bytes);
        when(mapGeneratorService.generateHeightmapPNG(eq(mapTopography))).thenReturn(byteOutputStream);
        when(mapGeneratorService.generateShadeMapPNG(eq(mapTopography))).thenReturn(byteOutputStream2);
        when(mapGeneratorService.generateContourMapPNG(eq(mapTopography))).thenReturn(byteOutputStream3);
        when(mapGeneratorService.generateSlopeMapPNG(eq(mapTopography))).thenReturn(byteOutputStream4);

        // Act
        final byte[] bytesToTest = serviceUnderTest.provideHeightMapPNG(gameMap);

        // Assert
        assertEquals(new String(bytes), new String(bytesToTest));
    }


}