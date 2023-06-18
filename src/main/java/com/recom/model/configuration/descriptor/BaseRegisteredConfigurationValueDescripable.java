package com.recom.model.configuration.descriptor;

import com.recom.model.configuration.ConfigurationType;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@RequiredArgsConstructor
public abstract class BaseRegisteredConfigurationValueDescripable {

    @NonNull
    private final String namespace;

    @NonNull
    private final String name;

    @NonNull
    private final String defaultValue;

    @NonNull
    public abstract ConfigurationType getType();

}
