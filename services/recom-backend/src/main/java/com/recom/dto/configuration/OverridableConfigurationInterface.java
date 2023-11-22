package com.recom.dto.configuration;

import com.recom.model.configuration.ConfigurationType;

public interface OverridableConfigurationInterface {

    String getNamespace();

    void setNamespace(final String namespace);

    String getName();

    void setName(final String name);

    ConfigurationType getType();

    void setType(final ConfigurationType type);

    String getMapOverriddenValue();

    void setMapOverriddenValue(final String mapOverriddenValue);


}
