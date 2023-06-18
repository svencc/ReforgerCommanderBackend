package com.recom.service.configuration;

import com.recom.dto.configuration.ConfigurationListDto;
import com.recom.entity.Configuration;
import com.recom.exception.ConfigurationNotReadableException;
import com.recom.model.configuration.ConfigurationType;
import com.recom.model.configuration.configurationvaluedescriptor.*;
import com.recom.repository.configuration.ConfigurationPersistenceLayer;
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
        final Optional<Configuration> mostConcrete = getMostConcreteConfiguration(mapName, descriptor);

        if (mostConcrete.isPresent() && mostConcrete.get().getType().equals(ConfigurationType.BOOLEAN)) {
            return Boolean.valueOf(mostConcrete.get().getValue());
        } else {
            throw new InvalidParameterException();
        }
    }

    @NonNull
    private Optional<Configuration> getMostConcreteConfiguration(
            @NonNull final String mapName,
            @NonNull final BaseRegisteredConfigurationValueDescripable descriptor
    ) {
        return queryValue(mapName, descriptor.getNamespace(), descriptor.getName()).stream()
                .min(Comparator.nullsLast(
                        Comparator.comparing(Configuration::getMapName, Comparator.nullsLast(String::compareTo))
                ));
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
        final Optional<Configuration> mostConcrete = getMostConcreteConfiguration(mapName, descriptor);

        if (mostConcrete.isPresent() && mostConcrete.get().getType().equals(ConfigurationType.STRING)) {
            return mostConcrete.get().getValue();
        }

        throw generateConfigurationNotReadableException(descriptor, Optional.empty());
    }

    @NonNull
    private ConfigurationNotReadableException generateConfigurationNotReadableException(
            @NonNull final BaseRegisteredConfigurationValueDescripable descriptor,
            @NonNull final Optional<Configuration> mostConcrete
    ) {
        return mostConcrete.map(configuration -> new ConfigurationNotReadableException(
                String.format("queryValue %s : %s type '%s' is not readable or value '%s' is not convertible!",
                        descriptor.getNamespace(),
                        descriptor.getName(),
                        descriptor.getType(),
                        configuration.getValue()
                ))).orElseGet(
                () -> new ConfigurationNotReadableException(
                        String.format("queryValue %s : %s type '%s' is not readable!",
                                descriptor.getNamespace(),
                                descriptor.getName(),
                                descriptor.getType()
                        ))
        );
    }

    @NonNull
    public Integer queryValue(
            @NonNull final String mapName,
            @NonNull final RegisteredIntegerConfigurationValueDescriptor descriptor
    ) {
        final Optional<Configuration> mostConcrete = getMostConcreteConfiguration(mapName, descriptor);

        if (mostConcrete.isPresent() && mostConcrete.get().getType().equals(ConfigurationType.INTEGER)) {
            try {
                return Integer.valueOf(mostConcrete.get().getValue());
            } catch (final NumberFormatException ignore) {
                // we throw an error at the end of function
            }
        }

        throw generateConfigurationNotReadableException(descriptor, mostConcrete);
    }

    @NonNull
    public Double queryValue(
            @NonNull final String mapName,
            @NonNull final RegisteredDoubleConfigurationValueDescriptor descriptor
    ) {
        final Optional<Configuration> mostConcrete = getMostConcreteConfiguration(mapName, descriptor);

        if (mostConcrete.isPresent() && mostConcrete.get().getType().equals(ConfigurationType.DOUBLE)) {
            try {
                return Double.valueOf(mostConcrete.get().getValue());
            } catch (final NumberFormatException ignore) {
                // we throw an error at the end of function
            }
        }

        throw generateConfigurationNotReadableException(descriptor, mostConcrete);
    }

    @NonNull
    public ConfigurationListDto provideAllExistingValueEntities(@NonNull final String mapName) {
        // @TODO to implement
        return ConfigurationListDto.builder().build();
    }

    @NonNull
    public ConfigurationListDto provideAllExistingValueEntities() {
        // @TODO to implement
        return ConfigurationListDto.builder().build();
    }
}
