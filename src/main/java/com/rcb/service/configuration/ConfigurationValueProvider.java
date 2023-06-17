package com.rcb.service.configuration;

import com.rcb.entity.Configuration;
import com.rcb.model.configuration.ConfigurationType;
import com.rcb.model.configuration.configurationvaluedescriptor.RegisteredBooleanConfigurationValueDescriptor;
import com.rcb.model.configuration.configurationvaluedescriptor.RegisteredDoubleConfigurationValueDescriptor;
import com.rcb.model.configuration.configurationvaluedescriptor.RegisteredIntegerConfigurationValueDescriptor;
import com.rcb.model.configuration.configurationvaluedescriptor.RegisteredStringConfigurationValueDescriptor;
import com.rcb.repository.configuration.ConfigurationPersistenceLayer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigurationValueProvider {

    @NonNull
    private final ConfigurationPersistenceLayer configurationPersistenceLayer;

    @NonNull
    protected List<Configuration> queryNamespace(
            @NonNull final String mapName,
            @NonNull final String namespace
    ) {
        // TODO ....
        return configurationPersistenceLayer.findNamespace(mapName, namespace);
    }

    @NonNull
    public List<Configuration> provideAllExistingDefaultValueEntities() {
        return configurationPersistenceLayer.findAllDefaultValueEntities();
    }

    @NonNull
    public Boolean queryValue(
            @NonNull final String mapName,
            @NonNull final RegisteredBooleanConfigurationValueDescriptor descriptor
    ) {
        final Optional<Configuration> mostConcrete = queryValue(mapName, descriptor.getNamespace(), descriptor.getName()).stream()
                .min(Comparator.nullsLast(Comparator.comparing(Configuration::getMapName)));

        if (mostConcrete.isPresent() && mostConcrete.get().getType().equals(ConfigurationType.BOOLEAN)) {
            return Boolean.valueOf(mostConcrete.get().getValue());
        } else {
            throw new InvalidParameterException();
        }
    }

    @NonNull
    protected List<Configuration> queryValue(
            @NonNull final String mapName,
            @NonNull final String namespace,
            @NonNull final String name
    ) {
        return configurationPersistenceLayer.findValues(mapName, namespace, name);
    }

    @NonNull
    public String queryValue(
            @NonNull final String mapName,
            @NonNull final RegisteredStringConfigurationValueDescriptor descriptor
    ) {
        final Optional<Configuration> mostConcrete = queryValue(mapName, descriptor.getNamespace(), descriptor.getName()).stream()
                .min(Comparator.nullsLast(Comparator.comparing(Configuration::getMapName)));

        if (mostConcrete.isPresent() && mostConcrete.get().getType().equals(ConfigurationType.STRING)) {
            return mostConcrete.get().getValue();
        } else {
            throw new InvalidParameterException();
        }
    }

    @NonNull
    public Integer queryValue(
            @NonNull final String mapName,
            @NonNull final RegisteredIntegerConfigurationValueDescriptor descriptor
    ) {
        final Optional<Configuration> mostConcrete = queryValue(mapName, descriptor.getNamespace(), descriptor.getName()).stream()
                .min(Comparator.nullsLast(Comparator.comparing(Configuration::getMapName)));

        if (mostConcrete.isPresent() && mostConcrete.get().getType().equals(ConfigurationType.INTEGER)) {
            return Integer.valueOf(mostConcrete.get().getValue());
        } else {
            throw new InvalidParameterException();
        }
    }

    public Double queryValue(
            @NonNull final String mapName,
            @NonNull final RegisteredDoubleConfigurationValueDescriptor descriptor
    ) {
        final Optional<Configuration> mostConcrete = queryValue(mapName, descriptor.getNamespace(), descriptor.getName()).stream()
                .min(Comparator.nullsLast(Comparator.comparing(Configuration::getMapName)));

        if (mostConcrete.isPresent() && mostConcrete.get().getType().equals(ConfigurationType.DOUBLE)) {
            return Double.valueOf(mostConcrete.get().getValue());
        } else {
            throw new InvalidParameterException();
        }
    }

}
