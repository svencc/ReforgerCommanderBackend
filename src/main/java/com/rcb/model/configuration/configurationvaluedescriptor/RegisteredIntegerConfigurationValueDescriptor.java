package com.rcb.model.configuration.configurationvaluedescriptor;

import com.rcb.model.configuration.ConfigurationType;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class RegisteredIntegerConfigurationValueDescriptor extends BaseRegisteredConfigurationValueDescripable {

    @Override
    public @NonNull ConfigurationType getType() {
        return ConfigurationType.INTEGER;
    }

}
