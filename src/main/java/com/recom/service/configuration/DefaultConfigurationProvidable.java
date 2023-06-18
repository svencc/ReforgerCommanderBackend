package com.recom.service.configuration;

import com.recom.model.configuration.descriptor.BaseRegisteredConfigurationValueDescripable;
import lombok.NonNull;

import java.util.List;

public interface DefaultConfigurationProvidable {

    @NonNull
    List<BaseRegisteredConfigurationValueDescripable> provideDefaultConfigurationValues();

}
