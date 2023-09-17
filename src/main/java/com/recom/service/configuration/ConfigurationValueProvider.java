package com.recom.service.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recom.entity.Configuration;
import com.recom.exception.ConfigurationNotReadableException;
import com.recom.model.configuration.ConfigurationType;
import com.recom.model.configuration.descriptor.*;
import com.recom.persistence.configuration.ConfigurationPersistenceLayer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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
    private final ObjectMapper objectMapper;

    @NonNull
    public Boolean queryValue(
            @NonNull final String mapName,
            @NonNull final RegisteredBooleanConfigurationValueDescriptor descriptor
    ) {
        final Optional<Configuration> maybeMostConcrete = queryMostConcreteConfiguration(mapName, descriptor);

        if (maybeMostConcrete.isPresent() && maybeMostConcrete.get().getType().equals(ConfigurationType.BOOLEAN)) {
            return Boolean.valueOf(maybeMostConcrete.get().getValue());
        } else {
            throw new InvalidParameterException();
        }
    }

    @NonNull
    public Optional<Configuration> queryMostConcreteConfiguration(
            @NonNull final String mapName,
            @NonNull final BaseRegisteredConfigurationValueDescribable descriptor
    ) {
        return queryValue(mapName, descriptor.getNamespace(), descriptor.getName()).stream()
                .min(Comparator.nullsLast(
                        Comparator.comparing(Configuration::getMapName, Comparator.nullsLast(String::compareTo))
                ));
    }

    @NonNull
    private List<Configuration> queryValue(
            @NonNull final String mapName,
            @NonNull final String namespace,
            @NonNull final String name
    ) {
        return configurationPersistenceLayer.findValues(mapName, namespace, name);
    }

//    @NonNull
//    private Map<String, List<Configuration>> preIndexConfigurationList(
//            List<Configuration> listToPreIndex
//    ) {
//        return listToPreIndex.stream()
//                .collect(Collectors.groupingBy(Configuration::getNamespace));
//    }

    @NonNull
    public String queryValue(
            @NonNull final String mapName,
            @NonNull final RegisteredStringConfigurationValueDescriptor descriptor
    ) {
        final Optional<Configuration> maybeMostConcrete = queryMostConcreteConfiguration(mapName, descriptor);

        if (maybeMostConcrete.isPresent() && maybeMostConcrete.get().getType().equals(ConfigurationType.STRING)) {
            return maybeMostConcrete.get().getValue();
        }

        throw generateConfigurationNotReadableException(descriptor, Optional.empty());
    }

    @NonNull
    private ConfigurationNotReadableException generateConfigurationNotReadableException(
            @NonNull final BaseRegisteredConfigurationValueDescribable descriptor,
            @NonNull final Optional<Configuration> maybeMostConcrete
    ) {
        return maybeMostConcrete.map(configuration -> new ConfigurationNotReadableException(
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
    @SneakyThrows(JsonProcessingException.class)
    public <TYPE> List<TYPE> queryValue(
            @NonNull final String mapName,
            @NonNull final RegisteredListConfigurationValueDescriptor<TYPE> descriptor
    ) {
        final Optional<Configuration> maybeMostConcrete = queryMostConcreteConfiguration(mapName, descriptor);

        if (maybeMostConcrete.isPresent() && maybeMostConcrete.get().getType().equals(ConfigurationType.LIST)) {
            return objectMapper.readValue(maybeMostConcrete.get().getValue(), new TypeReference<List<TYPE>>() {
            });
        }

        throw generateConfigurationNotReadableException(descriptor, Optional.empty());
    }

    @NonNull
    public Integer queryValue(
            @NonNull final String mapName,
            @NonNull final RegisteredIntegerConfigurationValueDescriptor descriptor
    ) {
        final Optional<Configuration> maybeMostConcrete = queryMostConcreteConfiguration(mapName, descriptor);

        if (maybeMostConcrete.isPresent() && maybeMostConcrete.get().getType().equals(ConfigurationType.INTEGER)) {
            try {
                return Integer.valueOf(maybeMostConcrete.get().getValue());
            } catch (final NumberFormatException ignore) {
                // we throw an error at the end of function
            }
        }

        throw generateConfigurationNotReadableException(descriptor, maybeMostConcrete);
    }

    @NonNull
    public Double queryValue(
            @NonNull final String mapName,
            @NonNull final RegisteredDoubleConfigurationValueDescriptor descriptor
    ) {
        final Optional<Configuration> maybeMostConcrete = queryMostConcreteConfiguration(mapName, descriptor);

        if (maybeMostConcrete.isPresent() && maybeMostConcrete.get().getType().equals(ConfigurationType.DOUBLE)) {
            try {
                return Double.valueOf(maybeMostConcrete.get().getValue());
            } catch (final NumberFormatException ignore) {
                log.error("queryValue {} : {} type '{}' is not readable or value '{}' is not convertible!",
                        descriptor.getNamespace(),
                        descriptor.getName(),
                        descriptor.getType(),
                        maybeMostConcrete.get().getValue()
                );
                // @TODO we throw an error at the end of function
            }
        }

        throw generateConfigurationNotReadableException(descriptor, maybeMostConcrete);
    }

    @NonNull
    public List<Configuration> provideAllExistingDefaultValues() {
        return configurationPersistenceLayer.findAllDefaultValueEntities();
    }

}
