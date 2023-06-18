package com.recom.model.configuration.configurationvaluedescriptor;

import com.recom.model.configuration.ConfigurationType;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class RegisteredStringConfigurationValueDescriptor extends BaseRegisteredConfigurationValueDescripable {

    @Override
    public @NonNull ConfigurationType getType() {
        return ConfigurationType.STRING;
    }

}
