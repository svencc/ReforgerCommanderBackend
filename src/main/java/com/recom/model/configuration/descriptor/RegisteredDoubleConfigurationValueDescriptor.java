package com.recom.model.configuration.descriptor;

import com.recom.model.configuration.ConfigurationType;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class RegisteredDoubleConfigurationValueDescriptor extends BaseRegisteredConfigurationValueDescripable {

    @Override
    public @NonNull ConfigurationType getType() {
        return ConfigurationType.DOUBLE;
    }

}
