package com.recom.service.map.topography;

import com.recom.commons.model.DEMDescriptor;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StepSizeCalculatorTest {

    final StepSizeCalculator serviceUnderTest = new StepSizeCalculator();


    @Test
    void calculateStepSize_when1_should1() {
        // Arrange
        final BigDecimal scaleFactor = new BigDecimal(1);
        final DEMDescriptor demDescriptor = DEMDescriptor.builder()
                .stepSize(new BigDecimal(1))
                .build();

        // Act
        final BigDecimal result = serviceUnderTest.calculateStepSize(demDescriptor, scaleFactor);

        // Assert
        assertEquals(new BigDecimal("1.0000000000"), result);
    }

    @Test
    void calculateStepSize_when2_should05() {
        // Arrange
        final BigDecimal scaleFactor = new BigDecimal(2);
        final DEMDescriptor demDescriptor = DEMDescriptor.builder()
                .stepSize(new BigDecimal(1))
                .build();

        // Act
        final BigDecimal result = serviceUnderTest.calculateStepSize(demDescriptor, scaleFactor);

        // Assert
        assertEquals(new BigDecimal("0.5000000000"), result);
    }

    @Test
    void calculateStepSize_when01_should10() {
        // Arrange
        final BigDecimal scaleFactor = new BigDecimal("0.1");
        final DEMDescriptor demDescriptor = DEMDescriptor.builder()
                .stepSize(new BigDecimal(1))
                .build();

        // Act
        final BigDecimal result = serviceUnderTest.calculateStepSize(demDescriptor, scaleFactor);

        // Assert
        assertEquals(new BigDecimal("10.0000000000"), result);
    }

    @Test
    void calculateStepSize_when005_should20() {
        // Arrange
        final BigDecimal scaleFactor = new BigDecimal("0.2");
        final DEMDescriptor demDescriptor = DEMDescriptor.builder()
                .stepSize(new BigDecimal(1))
                .build();

        // Act
        final BigDecimal result = serviceUnderTest.calculateStepSize(demDescriptor, scaleFactor);

        // Assert
        assertEquals(new BigDecimal("20.0000000000"), result);
    }

}