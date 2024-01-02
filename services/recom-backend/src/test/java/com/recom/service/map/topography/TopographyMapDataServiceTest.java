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
class TopographyMapDataServiceTest {

    @Mock
    private MapLocatedTopographyPersistenceLayer mapTopographyPersistenceLayer;
    @Mock
    private HeightmapGeneratorService heightmapGeneratorService;
    @Mock
    private SerializationService serializationService;
    @InjectMocks
    private TopographyMapDataService serviceUnderTest;


    @Test
    void provideTopographyMap() throws IOException {
        // Arrange
        final GameMap gameMap = GameMap.builder().name("test").build();

        final MapTopography mapTopography = MapTopography.builder()
                .gameMap(GameMap.builder().name("test").build())
                .build();
        when(mapTopographyPersistenceLayer.findByGameMap(eq(gameMap))).thenReturn(Optional.of(mapTopography));

        final ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        final byte[] bytes = {1, 2, 3};
        byteOutputStream.write(bytes);
        when(heightmapGeneratorService.generateHeightmap(eq(mapTopography))).thenReturn(byteOutputStream);

        // Act
        final byte[] bytesToTest = serviceUnderTest.provideTopographyPNG(gameMap);

        // Assert
        assertEquals(new String(bytes), new String(bytesToTest));
    }

    @Test
    void testProvideTopographyMap() throws IOException {
        // Arrange
        final MapTopography mapTopography = MapTopography.builder()
                .gameMap(GameMap.builder().name("test").build())
                .build();

        final ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        final byte[] bytes = {1, 2, 3};
        byteOutputStream.write(bytes);
        when(heightmapGeneratorService.generateHeightmap(eq(mapTopography))).thenReturn(byteOutputStream);

        // Act
        final byte[] bytesToTest = serviceUnderTest.provideTopographyPNG(mapTopography);

        // Assert
        assertEquals(new String(bytes), new String(bytesToTest));
    }

}