package com.recom.service;

import com.recom.testhelper.SerializeObjectHelper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SerializationServiceTest {

    private SerializationService serviceUnderTest;

    @BeforeEach
    void beforeEach() {
        serviceUnderTest = new SerializationService();
    }

    @Test
    void serializeObject() throws IOException {
        // Arrange
        final SerializableObject serializableObject = SerializableObject.builder()
                .name("testName")
                .age(10)
                .address("testAddress")
                .build();

        final ByteArrayOutputStream outputStream = SerializeObjectHelper.serializeObjectHelper(serializableObject);

        // Act
        final ByteArrayOutputStream outputStreamToTest = serviceUnderTest.serializeObject(serializableObject);

        // Assert
        assertEquals(new String(outputStream.toByteArray()), new String(outputStreamToTest.toByteArray()));
    }

    @Test
    void deserializeObject() throws IOException {
        // Arrange
        final SerializableObject serializableObject = SerializableObject.builder()
                .name("testName")
                .age(10)
                .address("testAddress")
                .build();

        final ByteArrayOutputStream outputStream = SerializeObjectHelper.serializeObjectHelper(serializableObject);

        // Act
        final Optional<SerializableObject> valueToTest = serviceUnderTest.deserializeObject(outputStream.toByteArray());

        // Assert
        assertTrue(valueToTest.isPresent());
        assertEquals(serializableObject.getName(), valueToTest.get().getName());
        assertEquals(serializableObject.getAge(), valueToTest.get().getAge());
        assertEquals(serializableObject.getAddress(), valueToTest.get().getAddress());
    }

    @Test
    @Disabled
    void readAndWriteBytesToAndFromFile() throws IOException {
        // Arrange
        final String testContent = "test";
        final byte[] byteArray = testContent.getBytes();
        final Path testFilePath = Path.of("src/test/resources/test");

        // Act
        serviceUnderTest.writeBytesToFile(testFilePath, byteArray);

        // Assert
        final byte[] bytesToTest = serviceUnderTest.readBytesFromFile(testFilePath);
        assertEquals(testContent, new String(bytesToTest));

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class SerializableObject implements Serializable {
        private String name;
        private int age;
        private String address;
    }

}