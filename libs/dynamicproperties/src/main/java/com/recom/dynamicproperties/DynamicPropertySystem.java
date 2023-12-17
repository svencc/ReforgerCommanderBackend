package com.recom.dynamicproperties;

import com.recom.dynamicproperties.exception.InitializationException;
import lombok.NonNull;
import org.springframework.core.convert.ConversionService;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

class DynamicPropertySystem<T extends DynamicProperties> {

    @NonNull
    private final Path basePath;
    @NonNull
    private final T dynamicPropertyObject;
    @NonNull
    private final ConversionService conversionService;


    public DynamicPropertySystem(
            @NonNull final T dynamicPropertyObject,
            @NonNull final ConversionService conversionService
    ) {
        this.basePath = dynamicPropertyObject.getPropertiesBasePath();
        this.dynamicPropertyObject = dynamicPropertyObject;
        this.conversionService = conversionService;
        loadOrCreateFromFilesystem();
    }

    public void loadOrCreateFromFilesystem() throws IllegalStateException {
        provideBaseDirectory();

        final File propertiesFile = getPropertyFilePath().toFile();
        if (!propertiesFile.exists()) {
            try {
                Assert.isTrue(propertiesFile.createNewFile(), String.format("Could not create file %1s", propertiesFile.getAbsolutePath()));
                createWithDefaultProperties();
                projectPropertiesToObject();
            } catch (IOException e) {
                throw new IllegalStateException(String.format("Could not create file %1s", propertiesFile.getAbsolutePath()));
            }
        } else {
            projectPropertiesToObject();
        }
    }

    private void provideBaseDirectory() {
        final File propertiesDirectory = getPropertiesPath().toFile();
        if (!propertiesDirectory.exists()) {
            Assert.isTrue(propertiesDirectory.mkdir(), String.format("Could not create directory %1s", propertiesDirectory.getAbsolutePath()));
        }

        Assert.isTrue(propertiesDirectory.isDirectory(), String.format("%1s is not a directory", propertiesDirectory.getAbsolutePath()));
        Assert.isTrue(propertiesDirectory.canRead(), String.format("%1s is not readable", propertiesDirectory.getAbsolutePath()));
        Assert.isTrue(propertiesDirectory.canWrite(), String.format("%1s is not writable", propertiesDirectory.getAbsolutePath()));
    }

    public void persistToFilesystem() throws IllegalStateException {
        provideBaseDirectory();
        persistObjectToProperties();
    }

    private void projectPropertiesToObject() {
        try (final FileReader reader = new FileReader(getPropertyFilePath().toFile(), StandardCharsets.UTF_8)) {
            dynamicPropertyObject.properties.load(reader);
            dynamicPropertyObject.listDynamicProperties().forEach((final Field field) -> {
                final boolean originalAccessibleState = field.canAccess(dynamicPropertyObject);
                field.setAccessible(true);

                final Object convertedValue = conversionService.convert(
                        dynamicPropertyObject.properties.getProperty(field.getName()),
                        field.getType()
                );
                try {
                    field.set(dynamicPropertyObject, convertedValue);
                } catch (IllegalAccessException e) {
                    throw new InitializationException(e.getMessage());
                }

                field.setAccessible(originalAccessibleState);
            });
        } catch (IOException ioe) {
            throw new InitializationException(ioe.getMessage());
        }
    }

    private void persistObjectToProperties() {
        try (final FileWriter writer = new FileWriter(getPropertyFilePath().toFile(), StandardCharsets.UTF_8)) {
            dynamicPropertyObject.listDynamicProperties().forEach((final Field field) -> {
                final boolean originalAccessibleState = field.canAccess(dynamicPropertyObject);
                field.setAccessible(true);

                try {
                    final Object dataItem = field.get(dynamicPropertyObject);
                    final String stringConvertedValue = conversionService.convert(dataItem, String.class);
                    dynamicPropertyObject.properties.setProperty(field.getName(), stringConvertedValue);
                } catch (IllegalAccessException e) {
                    throw new InitializationException(e.getMessage());
                }

                field.setAccessible(originalAccessibleState);
            });
            dynamicPropertyObject.properties.store(writer, "Written by DynamicPropertySystem");
        } catch (IOException ioe) {
            throw new InitializationException(ioe.getMessage());
        }
    }

    private void createWithDefaultProperties() throws InitializationException {
        copyValuesFromDefaultConstructedInstance();
        persistObjectToProperties();
    }

    /**
     * Creates a new instance of the same class as the dynamicPropertyObject and copies all values from the default instance to the dynamicPropertyObject.
     * This is done to ensure that all properties are present in the properties file.
     */
    private void copyValuesFromDefaultConstructedInstance() {
        // create a new instance of the same class as the dynamicPropertyObject
        final DynamicProperties dynamicPropertyWithDefaults;
        try {
            dynamicPropertyWithDefaults = dynamicPropertyObject.getClass().getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new InitializationException(e.getMessage());
        }

        // create a map of all fields of the default instance
        final Map<String, Field> indexedFieldsWithOriginalValues = dynamicPropertyWithDefaults.listDynamicProperties().stream()
                .peek((final Field field) -> field.setAccessible(true))
                .collect(Collectors.toMap(Field::getName, Function.identity()));

        // copy all values from the default instance to the dynamicPropertyObject
        dynamicPropertyObject.listDynamicProperties().forEach((final Field propertyField) -> {
            final boolean originalAccessibleState = propertyField.canAccess(dynamicPropertyObject);
            propertyField.setAccessible(true);
            final Field fieldWithDefaultValue = indexedFieldsWithOriginalValues.get(propertyField.getName());

            try {
                final Object defaultValue = fieldWithDefaultValue.get(dynamicPropertyWithDefaults);
                propertyField.set(dynamicPropertyObject, defaultValue);
            } catch (IllegalAccessException e) {
                throw new InitializationException(e.getMessage());
            }

            propertyField.setAccessible(originalAccessibleState);
        });
    }

    @NonNull
    private Path getPropertyFilePath() {
        return getPropertiesPath().resolve(String.format("%1s.properties", dynamicPropertyObject.getPropertyFileName()));
    }

    @NonNull
    public Path getPropertiesPath() throws IllegalStateException {
        return basePath.resolve(dynamicPropertyObject.getApplicationName());
    }

}
