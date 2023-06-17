package com.rcb.service;

import com.rcb.model.configuration.configurationvaluedescriptor.BaseRegisteredConfigurationValueDescripable;
import lombok.NonNull;

import java.util.List;

public interface DefaultConfigurationProvidable {

    @NonNull
    List<BaseRegisteredConfigurationValueDescripable> provideDefaultConfigurationValues();

}
