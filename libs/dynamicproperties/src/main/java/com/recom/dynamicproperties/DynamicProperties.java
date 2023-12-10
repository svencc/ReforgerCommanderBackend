package com.recom.dynamicproperties;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public abstract class DynamicProperties {

    @NonNull
    private static List<String> EXCLUDED_FIELDS = List.of(
            "this",
            "dynamicPropertySystem",
            "properties",
            "propertiesBasePath",
            "applicationName",
            "propertyFileName"
    );

    DynamicPropertySystem<? extends DynamicProperties> dynamicPropertySystem;

    final Properties properties = new Properties();



    @Nullable
    public Path getPropertiesBasePath() {
        return null;
    }

    public abstract String getApplicationName();

    abstract String getPropertyFileName();

    @NonNull
    List<Field> listDynamicProperties() {
        final List<Field> fields = Arrays.asList(getClass().getDeclaredFields());
        fields.removeIf(field -> EXCLUDED_FIELDS.contains(field.getName()));

        return fields;
    }

    public void persist() {
        dynamicPropertySystem.persistToFilesystem();
    }

    public void load() {
        dynamicPropertySystem.loadOrCreateFromFilesystem();
    }

}
