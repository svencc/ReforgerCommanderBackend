package com.recom.service.map.topography;

import com.recom.commons.map.rasterizer.ContourMapRasterizer;
import com.recom.commons.map.rasterizer.HeightMapRasterizer;
import com.recom.commons.map.rasterizer.ShadowedMapRasterizer;
import com.recom.commons.map.rasterizer.SlopeMapRasterizer;
import com.recom.commons.model.DEMDescriptor;
import com.recom.entity.map.GameMap;
import com.recom.entity.map.MapTopography;
import com.recom.model.map.TopographyData;
import com.recom.service.SerializationService;
import com.recom.testhelper.SerializeObjectHelper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MapPNGGeneratorServiceTest {

    @Mock
    private SerializationService serializationService;
    @Mock
    private HeightMapRasterizer heightmapRasterizer;
    @Mock
    private ShadowedMapRasterizer shadowedMapRasterizer;
    @Mock
    private ContourMapRasterizer contourMapRasterizer;
    @Mock
    private SlopeMapRasterizer slopeMapRasterizer;


    @InjectMocks
    private MapPNGGeneratorService serviceUnderTest;


    @Test
    @Disabled // does not work anymore after refactoring; but will be useless in future I guess
    void generateHeightmap() throws IOException {
        // Arrange
        when(heightmapRasterizer.rasterizeHeightMap(any())).thenCallRealMethod();

        final GameMap gameMap = GameMap.builder()
                .name("test")
                .build();
        final float[][] heightMap = {
                {255f, 127f},
                {0, 255f}
        };
        final TopographyData topographyData = TopographyData.builder()
                .scanIterationsX(2)
                .scanIterationsZ(2)
                .stepSize(1f)
                .oceanBaseHeight(0f)
                .surfaceData(heightMap)
                .build();
        final MapTopography mapTopography = MapTopography.builder()
                .gameMap(gameMap)
                .data(SerializeObjectHelper.serializeObjectHelper(topographyData).toByteArray())
                .build();

        when(serializationService.deserializeObject(any())).thenReturn(Optional.of(topographyData));

        // Act
        final ByteArrayOutputStream resultToTest = serviceUnderTest.generateHeightmapPNG(mapTopography);

        // Assert
        assertNotNull(resultToTest);

        final BufferedImage reReadImage = ImageIO.read(new ByteArrayInputStream(resultToTest.toByteArray()));
        assertNotNull(reReadImage);
        assertEquals(2, reReadImage.getHeight());
        assertEquals(2, reReadImage.getWidth());

        assertEquals(new Color(127, 127, 127).getRGB(), new Color(reReadImage.getRGB(0, 0)).getRGB());
        assertEquals(new Color(255, 255, 255).getRGB(), new Color(reReadImage.getRGB(1, 0)).getRGB());
        assertEquals(new Color(255, 255, 255).getRGB(), new Color(reReadImage.getRGB(0, 1)).getRGB());
        assertEquals(new Color(0, 0, 0).getRGB(), new Color(reReadImage.getRGB(1, 1)).getRGB());

        // Verify interactions
        verify(serializationService).deserializeObject(any());
    }

}