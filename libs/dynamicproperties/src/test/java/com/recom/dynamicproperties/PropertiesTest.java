package com.recom.dynamicproperties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.ConversionService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class PropertiesTest {

    final static Path TEST_BASE_PATH = Path.of("src/test/resources/testpath");
    final static Path PATH_TO_TESTFILE = TEST_BASE_PATH.resolve("RECOMCommander").resolve("file.properties");

    private PropertyFileBinder propertyFileBinder;
    private ConversionService conversionService;

    @BeforeEach
    void beforeEach() {
        final ConversionServiceFactoryBean bean = new ConversionServiceFactoryBean();
        bean.afterPropertiesSet();
        conversionService = bean.getObject();

        propertyFileBinder = new PropertyFileBinder(conversionService);
    }

    @Test
    void testCase() throws IOException {
        // Arrange
        final File fileToCleanUpInAdvance = PATH_TO_TESTFILE.toFile();
        if (fileToCleanUpInAdvance.exists()) {
            fileToCleanUpInAdvance.delete();
        }
        final TestProperties testProperties = new TestProperties();

        // Act 1
        propertyFileBinder.bindToFilesystem(testProperties); // if not extists, create with default values

        // Assert Object
        assertEquals("http", testProperties.getProtocol());
        assertEquals("localhost", testProperties.getHostname());
        assertEquals("8080", testProperties.getPort());

        // Assert File Content; check Default Values
        String content = Files.readString(PATH_TO_TESTFILE);
        assertTrue(content.contains("protocol=http"));
        assertTrue(content.contains("hostname=localhost"));
        assertTrue(content.contains("port=8080"));


        // Act 2
        testProperties.setProtocol("1");
        testProperties.setHostname("2");
        testProperties.setPort("3");
        testProperties.persist();

        // Assert Object & File after persist with new values
        content = Files.readString(PATH_TO_TESTFILE);
        assertTrue(content.contains("protocol=1"));
        assertTrue(content.contains("hostname=2"));
        assertTrue(content.contains("port=3"));


        // Act 3
        // change file content manually/emulate external change
        String modifiedContent = Files.readString(PATH_TO_TESTFILE);
        modifiedContent = modifiedContent.replace("protocol=1", "protocol=11");
        modifiedContent = modifiedContent.replace("hostname=2", "hostname=22");
        modifiedContent = modifiedContent.replace("port=3", "port=33");
        Files.writeString(PATH_TO_TESTFILE, modifiedContent);

        // Assert reread Object & File after external change
        testProperties.load();
        content = Files.readString(PATH_TO_TESTFILE);
        assertEquals("11", testProperties.getProtocol());
        assertEquals("22", testProperties.getHostname());
        assertEquals("33", testProperties.getPort());
    }

}