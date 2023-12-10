package com.recom.dynamicproperties;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public abstract class DynamicProperties {

    DynamicPropertySystem<? extends DynamicProperties> dynamicPropertySystem;

    final Properties properties = new Properties();


    public abstract String getApplicationName();

    @Nullable
    Path getPropertiesPath() {
        return null;
    }

    abstract String getPropertyFileName();

    @NonNull
    List<Field> listDynamicProperties() {
        return Arrays.asList(getClass().getDeclaredFields());
    }

    public void persist() {
        dynamicPropertySystem.persistToFilesystem();
    }

    public void load() {
        dynamicPropertySystem.loadOrCreateFromFilesystem();
    }

}
