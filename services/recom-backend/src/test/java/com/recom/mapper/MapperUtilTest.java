package com.recom.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recom.service.provider.StaticObjectMapperProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MapperUtilTest {

    @BeforeEach
    public void beforeEach() {
        final StaticObjectMapperProvider staticObjectMapperProvider = new StaticObjectMapperProvider(new ObjectMapper());
        staticObjectMapperProvider.postConstruct();
    }

    @Test
    public void testEncodeVectorToJsonString_withVector_thenReturnString() throws JsonProcessingException {
        // Arrange
        final List<BigDecimal> vectorXYZ = new ArrayList<>();
        vectorXYZ.add(BigDecimal.valueOf(1.0));
        vectorXYZ.add(BigDecimal.valueOf(2.0));
        vectorXYZ.add(BigDecimal.valueOf(3.0));

        // Act
        final String jsonToTest = MapperUtil.encodeVectorToJsonString(vectorXYZ);

        // Assert
        assertNotNull(jsonToTest);
    }

    @Test
    public void testEncodeVectorToJsonString_withNull_thenReturnNull() throws JsonProcessingException {
        // Act & Assert
        assertNull(MapperUtil.encodeVectorToJsonString(null));
    }

    @Test
    public void testDecodeJsonStringToVector_withVectorString_thenReturnList() throws JsonProcessingException {
        // Arrange
        final String json = "[1.0, 2.0, 3.0]";

        // Act
        final List<BigDecimal> vectorXYZ = MapperUtil.decodeJsonStringToVector(json);

        // Assert
        assertNotNull(vectorXYZ);
        assertEquals(3, vectorXYZ.size());
        assertEquals(BigDecimal.valueOf(1.0), vectorXYZ.get(0));
        assertEquals(BigDecimal.valueOf(2.0), vectorXYZ.get(1));
        assertEquals(BigDecimal.valueOf(3.0), vectorXYZ.get(2));
    }

    @Test
    public void testDecodeJsonStringToVector_withNull_thenReturnNull() throws JsonProcessingException {
        // Act & Assert
        assertNull(MapperUtil.decodeJsonStringToVector(null));
    }

    @Test
    public void testBlankStringToNull() throws JsonProcessingException {
        // Arrange & Act & Assert
        assertNull(MapperUtil.blankStringToNull("  "));
        assertEquals("Hello", MapperUtil.blankStringToNull("Hello"));
        assertNull(MapperUtil.blankStringToNull(null));
    }

    @Test
    public void testExtractCoordinateX() {
        // Arrange & Act & Assert
        assertNull(MapperUtil.extractCoordinateX(null));
        assertEquals(BigDecimal.ONE, MapperUtil.extractCoordinateX(List.of(BigDecimal.ONE, BigDecimal.TWO, BigDecimal.TEN)));
    }

    @Test
    public void testExtractCoordinateY() {
        // Arrange & Act & Assert
        assertNull(MapperUtil.extractCoordinateY(null));
        assertEquals(BigDecimal.TWO, MapperUtil.extractCoordinateY(List.of(BigDecimal.ONE, BigDecimal.TWO, BigDecimal.TEN)));
    }

    @Test
    public void testExtractCoordinateZ() {
        // Arrange & Act & Assert
        assertNull(MapperUtil.extractCoordinateZ(null));
        assertEquals(BigDecimal.TEN, MapperUtil.extractCoordinateZ(List.of(BigDecimal.ONE, BigDecimal.TWO, BigDecimal.TEN)));
    }

}