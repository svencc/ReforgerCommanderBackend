package com.recom.dynamicproperties;

import org.springframework.lang.NonNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.ArrayList;
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
            "propertyFileName",
            "bufferedSubject"
    );

    DynamicPropertySystem<? extends DynamicProperties> dynamicPropertySystem;

    final Properties properties = new Properties();


    @NonNull
    public abstract Path getPropertiesBasePath();

    @NonNull
    public abstract String getApplicationName();

    @NonNull
    public abstract String getPropertyFileName();

    @NonNull
    List<Field> listDynamicProperties() {
        final List<Field> fields = new ArrayList<>(Arrays.asList(getClass().getDeclaredFields()));
        fields.removeIf(field -> EXCLUDED_FIELDS.contains(field.getName()) || Modifier.isStatic(field.getModifiers()));

        return fields;
    }

    public void persist() {
        dynamicPropertySystem.persistToFilesystem();
    }

    public void load() {
        dynamicPropertySystem.loadOrCreateFromFilesystem();
    }

}
