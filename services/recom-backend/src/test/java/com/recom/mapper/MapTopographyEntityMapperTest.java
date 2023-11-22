//package com.recom.mapper;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.recom.dto.com.recom.dto.map.scanner.topography.MapTopographyEntityDto;
//import com.recom.entity.MapTopographyEntity;
//import com.recom.service.provider.StaticObjectMapperProvider;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.math.BigDecimal;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//class MapTopographyEntityMapperTest {
//
//    ObjectMapper objectMapper;
//
//    @BeforeEach
//    public void beforeEach() {
//        objectMapper = new ObjectMapper();
//        final StaticObjectMapperProvider staticObjectMapperProvider = new StaticObjectMapperProvider(objectMapper);
//        staticObjectMapperProvider.postConstruct();
//    }
//
//    @Test
//    public void testToEntity() throws JsonProcessingException {
//        // Arrange
//        final Float oceanHeight = 0F;
//        final Float oceanBaseHeight = 5F;
//        final List<BigDecimal> coordinates = List.of(BigDecimal.valueOf(1.0), BigDecimal.valueOf(2.0), BigDecimal.valueOf(3.0));
//
//        final MapTopographyEntityDto dto = MapTopographyEntityDto.builder()
//                .oceanHeight(oceanHeight)
//                .oceanBaseHeight(oceanBaseHeight)
//                .coordinates(coordinates)
//                .build();
//
//        // Act
//        final MapTopographyEntity entityToTest = MapTopographyEntity.builder().build(); // MapTopographyEntityMapper.INSTANCE.toEntity(dto); <<<<<<<<<<<<<<<<<<<<<<<<<
//
//        // Assert
//        assertEquals(oceanHeight, entityToTest.getOceanHeight());
//        assertEquals(oceanBaseHeight, entityToTest.getOceanBaseHeight());
//        assertEquals(BigDecimal.valueOf(1.0), entityToTest.getCoordinateX());
//        assertEquals(BigDecimal.valueOf(2.0), entityToTest.getCoordinateY());
//        assertEquals(BigDecimal.valueOf(3.0), entityToTest.getCoordinateZ());
//    }
//
//    @Test
//    public void testToDto() throws JsonProcessingException {
//        // Arrange
//        final Float oceanHeight = 0F;
//        final Float oceanBaseHeight = 5F;
//
//        final MapTopographyEntity entity = MapTopographyEntity.builder()
//                .oceanHeight(oceanHeight)
//                .oceanBaseHeight(oceanBaseHeight)
//                .coordinateX(BigDecimal.valueOf(1.0))
//                .coordinateY(BigDecimal.valueOf(2.0))
//                .coordinateZ(BigDecimal.valueOf(3.0))
//                .build();
//
//        // Act
//        final MapTopographyEntityDto dtoToTest = MapTopographyEntityDto.builder().build(); //MapTopographyEntityMapper.INSTANCE.toDto(entity);
//
//        // Assert
//        assertEquals(oceanHeight, dtoToTest.getOceanHeight());
//        assertEquals(oceanBaseHeight, dtoToTest.getOceanBaseHeight());
////        assertEquals(BigDecimal.valueOf(1.0), dtoToTest.getCoordinates().get(0));
////        assertEquals(BigDecimal.valueOf(2.0), dtoToTest.getCoordinates().get(1));
////        assertEquals(BigDecimal.valueOf(3.0), dtoToTest.getCoordinates().get(2)); <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
//    }
//
//}