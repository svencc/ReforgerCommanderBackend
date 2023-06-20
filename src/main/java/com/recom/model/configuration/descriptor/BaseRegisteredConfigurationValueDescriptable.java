package com.recom.model.configuration.descriptor;

import com.recom.model.configuration.ConfigurationType;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@RequiredArgsConstructor
public abstract class BaseRegisteredConfigurationValueDescriptable {

    @NonNull
    private final String namespace;

    @NonNull
    private final String name;

    @NonNull
    private final String defaultValue;

    @NonNull
    @Builder.Default
    private final Boolean enabled = false;

    @NonNull
    public abstract ConfigurationType getType();

}
