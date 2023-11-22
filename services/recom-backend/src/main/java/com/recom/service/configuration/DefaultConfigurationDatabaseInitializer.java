package com.recom.service.configuration;

import com.recom.entity.Configuration;
import com.recom.model.configuration.descriptor.RegisteredConfigurationValueDescribtable;
import com.recom.persistence.configuration.ConfigurationPersistenceLayer;
import com.recom.runner.PostStartupExecutor;
import com.recom.service.PostStartExecutable;
import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultConfigurationDatabaseInitializer implements PostStartExecutable {

    @NonNull
    private final PostStartupExecutor postStartupExecutor;
    @NonNull
    private final ConfigurationPersistenceLayer configurationPersistenceLayer;
    @NonNull
    private final ConfigurationValueProvider configurationValueProvider;

    @NonNull
    private final List<DefaultConfigurationProvidable> defaultConfigurationProviderRegister = new ArrayList<>();

    @PostConstruct
    public void postConstruct() {
        postStartupExecutor.registerPostStartRunner(this);
    }

    public void registerDefaultConfigurationProvider(@NonNull final DefaultConfigurationProvidable provider) {
        defaultConfigurationProviderRegister.add(provider);
    }

    @Override
    public @NonNull String identifyPostStartRunner() {
        return "Apply registered default configurations to the database.";
    }

    @Override
    @Transactional(readOnly = false)
    public void executePostStartRunner() {
        applyRegisteredDefaultSettingsToDatabase();
    }

    private void applyRegisteredDefaultSettingsToDatabase() {
        final List<RegisteredConfigurationValueDescribtable> allRegisteredDefaultValues = defaultConfigurationProviderRegister.stream()
                .flatMap((provider) -> provider.provideDefaultConfigurationValues().stream())
                .toList();

        final Map<String, Map<String, List<Configuration>>> indexedExistingConfigurationList = configurationValueProvider.provideAllExistingDefaultValues().stream()
                .collect(Collectors.groupingBy(Configuration::getNamespace, Collectors.groupingBy(Configuration::getName)));

        final List<Configuration> configurationsToCreate = new ArrayList<>();
        final List<Configuration> configurationsToUpdate = new ArrayList<>();
        final List<Configuration> configurationsToDelete = new ArrayList<>();

        allRegisteredDefaultValues.forEach((final RegisteredConfigurationValueDescribtable registeredDefaultValue) -> {
            final String namespace = registeredDefaultValue.getNamespace();
            final String name = registeredDefaultValue.getName();

            final Optional<Configuration> maybeExistingConfiguration = findConfigurationInIndexedMap(indexedExistingConfigurationList, namespace, name);
            if (maybeExistingConfiguration.isPresent()) {
                maybeExistingConfiguration.get().setValue(registeredDefaultValue.getDefaultValue());
                maybeExistingConfiguration.get().setType(registeredDefaultValue.getType());
                configurationsToUpdate.add(maybeExistingConfiguration.get());
            } else {
                Configuration newConfiguration = Configuration.builder()
                        .namespace(registeredDefaultValue.getNamespace())
                        .name(registeredDefaultValue.getName())
                        .type(registeredDefaultValue.getType())
                        .value(registeredDefaultValue.getDefaultValue())
                        .build();

                configurationsToCreate.add(newConfiguration);
            }
        });

        final Map<String, Map<String, List<Configuration>>> configurationsToKeepOrCreate = Stream.concat(configurationsToCreate.stream(), configurationsToUpdate.stream())
                .collect(Collectors.groupingBy(Configuration::getNamespace, Collectors.groupingBy(Configuration::getName)));

        configurationValueProvider.provideAllExistingDefaultValues().forEach(
                (final Configuration existingDefaultValue) -> {
                    findConfigurationInIndexedMap(configurationsToKeepOrCreate, existingDefaultValue.getNamespace(), existingDefaultValue.getName())
                            .ifPresentOrElse(
                                    (configuration) -> {
                                        // do nothing
                                    },
                                    () -> {
                                        configurationsToDelete.add(existingDefaultValue);
                                    }
                            );
                });

        configurationPersistenceLayer.saveAll(configurationsToCreate);
        configurationPersistenceLayer.saveAll(configurationsToUpdate);
        configurationPersistenceLayer.deleteAll(configurationsToDelete);
    }

    @NonNull
    private Optional<Configuration> findConfigurationInIndexedMap(
            @NonNull final Map<String, Map<String, List<Configuration>>> preIndexedConfigurationList,
            @NonNull final String namespace,
            @NonNull final String name
    ) {
        // @TODO: probably doubled code; see ConfigurationRESTManagementService.findConfigurationInIndexedMap  -> check, test and refactor
        return preIndexedConfigurationList.getOrDefault(namespace, Collections.emptyMap())
                .getOrDefault(name, Collections.emptyList()).stream()
                .findFirst();
    }

}
