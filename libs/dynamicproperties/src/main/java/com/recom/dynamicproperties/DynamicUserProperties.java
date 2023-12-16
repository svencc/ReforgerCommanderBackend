package com.recom.dynamicproperties;

import org.springframework.lang.NonNull;

import java.nio.file.Path;
import java.util.Optional;

public abstract class DynamicUserProperties extends DynamicProperties {

    @NonNull
    @Override
    public Path getPropertiesBasePath() {
        return Path.of(Optional.ofNullable(System.getProperty("user.home"))
                .orElseThrow(() -> Exceptional.whenPropertyNotSet("user.home")));
    }

    @NonNull
    @Override
    public abstract String getPropertyFileName();

}
