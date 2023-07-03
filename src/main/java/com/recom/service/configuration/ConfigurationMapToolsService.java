package com.recom.service.configuration;

import com.recom.entity.Configuration;
import com.recom.event.event.async.cache.CacheResetAsyncEvent;
import com.recom.model.configuration.descriptor.RegisteredListConfigurationValueDescriptor;
import com.recom.repository.configuration.ConfigurationPersistenceLayer;
import com.recom.service.map.MapMetaDataService;
import com.recom.service.provider.StaticObjectMapperProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigurationMapToolsService {

    final Predicate<Optional<Configuration>> isDefaultListValue = (confOpt) -> confOpt.isPresent() && confOpt.get().getMapName() != null;
    @NonNull
    private final MapMetaDataService mapMetaDataService;
    @NonNull
    private final ConfigurationValueProvider configurationValueProvider;
    @NonNull
    private final ApplicationEventPublisher applicationEventPublisher;
    @NonNull
    private final ConfigurationPersistenceLayer configurationPersistenceLayer;

    @NonNull
    @Transactional(readOnly = false)
    public List<String> addResources(
            @NonNull final String mapName,
            @NonNull final List<String> addResourcesMatcherList,
            @NonNull final RegisteredListConfigurationValueDescriptor<String> listConfigurationValueDescriptor
    ) {
        final AddResourcesTuple resultTuple = mergeWithExistingConfigurationList(mapName, addResourcesMatcherList, listConfigurationValueDescriptor);
        overrideListValue(mapName, listConfigurationValueDescriptor, resultTuple.mergedListToPersist());
        applicationEventPublisher.publishEvent(new CacheResetAsyncEvent());

        return resultTuple.resourcesToAdd().stream()
                .sorted()
                .toList();
    }

    @NonNull
    private ConfigurationMapToolsService.AddResourcesTuple mergeWithExistingConfigurationList(
            @NonNull final String mapName,
            @NonNull final List<String> addResourcesMatcherList,
            @NonNull final RegisteredListConfigurationValueDescriptor<String> configurationValueDescriptor
    ) {
        final Set<String> resourcesToAdd = new HashSet<>();
        addResourcesMatcherList.stream()
                .distinct()
                .forEach(entityMatcher -> {
                    final List<String> matchedEntities = mapMetaDataService.provideMapMeta(mapName).getUtilizedResources().stream()
                            .filter(utilizedClass -> utilizedClass.toLowerCase().matches(entityMatcher.toLowerCase()))
                            .toList();

                    resourcesToAdd.addAll(matchedEntities);
                });

        final List<String> existingClusterResources = configurationValueProvider.queryValue(mapName, configurationValueDescriptor);

        List<String> mergedResourceList = new ArrayList<>(existingClusterResources);
        mergedResourceList.addAll(resourcesToAdd);
        mergedResourceList = mergedResourceList.stream()
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        return new AddResourcesTuple(resourcesToAdd, mergedResourceList);
    }

    @SneakyThrows
    public <T> void overrideListValue(
            @NonNull final String mapName,
            @NonNull final RegisteredListConfigurationValueDescriptor<T> configurationValueDescribable,
            @NonNull final List<T> mergedList
    ) {
        final Optional<Configuration> configurationOpt = configurationValueProvider.queryMostConcreteConfiguration(mapName, configurationValueDescribable);

        if (isDefaultListValue.test(configurationOpt)) {
            configurationOpt.get().setValue(StaticObjectMapperProvider.provide().writeValueAsString(mergedList));

            configurationPersistenceLayer.save(configurationOpt.get());
        } else {
            // @TODO this is a candidate for a service ... this code is already duplicated multiple times.
            final Configuration.ConfigurationBuilder configurationBuilder = Configuration.builder()
                    .mapName(mapName)
                    .namespace(configurationValueDescribable.getNamespace())
                    .name(configurationValueDescribable.getName())
                    .type(configurationValueDescribable.getType())
                    .value(StaticObjectMapperProvider.provide().writeValueAsString(mergedList));

            configurationPersistenceLayer.save(configurationBuilder.build());
        }
    }

    @NonNull
    @Transactional(readOnly = false)
    public List<String> removeResources(
            @NonNull final String mapName,
            @NonNull final List<String> removeResourcesMatcherList,
            @NonNull final RegisteredListConfigurationValueDescriptor<String> listConfigurationValueDescriptor
    ) {
        final RemoveResourcesTuple resultTuple = removeFromExistingConfigurationList(mapName, removeResourcesMatcherList, listConfigurationValueDescriptor);
        overrideListValue(mapName, listConfigurationValueDescriptor, resultTuple.mergedListToPersist());
        applicationEventPublisher.publishEvent(new CacheResetAsyncEvent());

        return resultTuple.resourcesToRemove().stream()
                .sorted()
                .toList();
    }

    @NonNull
    private ConfigurationMapToolsService.RemoveResourcesTuple removeFromExistingConfigurationList(
            @NonNull final String mapName,
            @NonNull final List<String> removeResourcesMatcherList,
            @NonNull final RegisteredListConfigurationValueDescriptor<String> configurationValueDescriptor
    ) {
        final Set<String> resourcesToRemove = new HashSet<>();
        removeResourcesMatcherList.stream()
                .distinct()
                .forEach(entityMatcher -> {
                    final List<String> matchedEntities = mapMetaDataService.provideMapMeta(mapName).getUtilizedResources().stream()
                            .filter(utilizedClass -> utilizedClass.toLowerCase().matches(entityMatcher.toLowerCase()))
                            .toList();

                    resourcesToRemove.addAll(matchedEntities);
                });

        final List<String> existingClusterResources = configurationValueProvider.queryValue(mapName, configurationValueDescriptor);
        existingClusterResources.removeIf(resourcesToRemove::contains);

        return new RemoveResourcesTuple(resourcesToRemove, existingClusterResources);
    }

    private record AddResourcesTuple(Set<String> resourcesToAdd, List<String> mergedListToPersist) {
    }

    private record RemoveResourcesTuple(Set<String> resourcesToRemove, List<String> mergedListToPersist) {
    }

}
