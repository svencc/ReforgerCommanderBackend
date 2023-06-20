package com.recom.service.configuration;

import com.recom.model.configuration.descriptor.BaseRegisteredConfigurationValueDescriptable;
import lombok.NonNull;

import java.util.List;

public interface DefaultConfigurationProvidable {

    @NonNull
    List<BaseRegisteredConfigurationValueDescriptable> provideDefaultConfigurationValues();

}
