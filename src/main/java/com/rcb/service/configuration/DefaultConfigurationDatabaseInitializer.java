package com.rcb.service.configuration;

import com.rcb.PostStartupExecutor;
import com.rcb.entity.Configuration;
import com.rcb.model.configuration.configurationvaluedescriptor.BaseRegisteredConfigurationValueDescripable;
import com.rcb.repository.configuration.ConfigurationPersistenceLayer;
import com.rcb.service.PostStartExecutable;
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
        applyRegisteredDefaultSettings();
    }

    protected void applyRegisteredDefaultSettings() {
        final List<BaseRegisteredConfigurationValueDescripable> allRegisteredDefaultValues = defaultConfigurationProviderRegister.stream()
                .flatMap((provider) -> provider.provideDefaultConfigurationValues().stream())
                .toList();

        final Map<String, Map<String, List<Configuration>>> allExistingDefaultValues = configurationValueProvider.provideAllExistingDefaultValueEntities().stream()
                .collect(Collectors.groupingBy(Configuration::getNamespace, Collectors.groupingBy(Configuration::getName)));

        final List<Configuration> configurationsToCreate = new ArrayList<>();
        final List<Configuration> configurationsToUpdate = new ArrayList<>();
        final List<Configuration> configurationsToDelete = new ArrayList<>();

        allRegisteredDefaultValues.forEach((final BaseRegisteredConfigurationValueDescripable registeredDefaultValue) -> {
            final String namespace = registeredDefaultValue.getNamespace();
            final String name = registeredDefaultValue.getName();

            final Optional<Configuration> existingConfigurationOpt = findExistingValue(allExistingDefaultValues, namespace, name);
            if (existingConfigurationOpt.isPresent()) {
                existingConfigurationOpt.get().setValue(registeredDefaultValue.getDefaultValue());
                existingConfigurationOpt.get().setType(registeredDefaultValue.getType());
                configurationsToUpdate.add(existingConfigurationOpt.get());
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

        configurationValueProvider.provideAllExistingDefaultValueEntities().forEach(
                (final Configuration existingDefaultValue) -> {
                    findExistingValue(configurationsToKeepOrCreate, existingDefaultValue.getNamespace(), existingDefaultValue.getName())
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
    protected Optional<Configuration> findExistingValue(
            @NonNull final Map<String, Map<String, List<Configuration>>> allExistingDefaultValues,
            @NonNull final String namespace,
            @NonNull final String name
    ) {
        return allExistingDefaultValues.getOrDefault(namespace, Collections.emptyMap())
                .getOrDefault(name, Collections.emptyList()).stream()
                .findFirst();
    }

    @NonNull
    protected List<Configuration> findExistingValues(
            @NonNull final Map<String, Map<String, List<Configuration>>> allExistingDefaultValues,
            @NonNull final String namespace
    ) {
        return allExistingDefaultValues.getOrDefault(namespace, Collections.emptyMap())
                .values().stream()
                .flatMap(Collection::stream)
                .toList();
    }

}
