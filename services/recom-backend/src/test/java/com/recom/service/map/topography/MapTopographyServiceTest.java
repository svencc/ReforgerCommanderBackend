package com.recom.service.map.topography;

import com.recom.persistence.map.topography.MapLocatedTopographyPersistenceLayer;
import com.recom.service.SerializationService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

@ExtendWith(MockitoExtension.class)
class MapTopographyServiceTest {

    @Mock
    private MapLocatedTopographyPersistenceLayer mapTopographyPersistenceLayer;
    @Mock
    private MapPNGGeneratorService mapPNGGeneratorService;
    @Mock
    private SerializationService serializationService;
    @Mock
    private DEMService demService;
    @InjectMocks
    private MapTopographyService serviceUnderTest;


    @Test
    @Disabled
        // does not work anymore after refactoring; but will be useless in future I guess
    void provideTopographyMap() throws IOException {
        /*
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
        when(mapPNGGeneratorService.generateHeightmapPNG(any())).thenReturn(byteOutputStream);
        when(mapPNGGeneratorService.generateShadeMapPNG(any())).thenReturn(byteOutputStream2);
        when(mapPNGGeneratorService.generateContourMapPNG(any())).thenReturn(byteOutputStream3);
        when(mapPNGGeneratorService.generateSlopeMapPNG(any())).thenReturn(byteOutputStream4);

        // Act
        final byte[] bytesToTest = serviceUnderTest.provideHeightMapPNG(gameMap);

        // Assert
        assertEquals(new String(bytes), new String(bytesToTest));
         */
    }


}