package com.recom.dynamicproperties;

import java.nio.file.Path;
import java.util.Optional;

public abstract class DynamicUserProperties extends DynamicProperties {

    @Override
    public Path getPropertiesBasePath() {
        return Path.of(Optional.ofNullable(System.getProperty("user.home"))
                .orElseThrow(() -> Exceptional.whenPropertyNotSet("user.home")));
    }

    @Override
    public abstract String getPropertyFileName();

}
