package com.recom.commander.configuration;

import com.recom.commander.property.user.HostProperties;
import com.recom.dynamicproperties.DynamicProperties;
import com.recom.dynamicproperties.PropertyBinder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@RequiredArgsConstructor
public class PropertiesFactory {

    @NonNull
    private final PropertyBinder propertyBinder;


    @Bean
    public HostProperties hostProperties() {
        return bind(new HostProperties());
    }

    @NonNull
    private <T extends DynamicProperties> T bind(@NonNull final T properties) {
        propertyBinder.bindToFilesystem(properties);

        return properties;
    }

}
