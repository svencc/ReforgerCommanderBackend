package com.recom.service.configuration;

import com.recom.model.configuration.descriptor.RegisteredConfigurationValueDescribtable;
import lombok.NonNull;

import java.util.List;

public interface DefaultConfigurationProvidable {

    @NonNull
    List<RegisteredConfigurationValueDescribtable> provideDefaultConfigurationValues();

}
