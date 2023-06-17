package com.rcb.service.configuration;

import com.rcb.model.configuration.configurationvaluedescriptor.BaseRegisteredConfigurationValueDescripable;
import lombok.NonNull;

import java.util.List;

public interface DefaultConfigurationProvidable {

    @NonNull
    List<BaseRegisteredConfigurationValueDescripable> provideDefaultConfigurationValues();

}
