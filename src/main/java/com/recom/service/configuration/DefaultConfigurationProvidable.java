package com.recom.service.configuration;

import com.recom.model.configuration.configurationvaluedescriptor.BaseRegisteredConfigurationValueDescripable;
import lombok.NonNull;

import java.util.List;

public interface DefaultConfigurationProvidable {

    @NonNull
    List<BaseRegisteredConfigurationValueDescripable> provideDefaultConfigurationValues();

}
