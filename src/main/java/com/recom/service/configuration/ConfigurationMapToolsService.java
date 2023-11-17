package com.recom.service.configuration;

import com.recom.entity.Configuration;
import com.recom.entity.GameMap;
import com.recom.entity.MapStructureEntity;
import com.recom.event.event.async.cache.CacheResetAsyncEvent;
import com.recom.model.configuration.descriptor.RegisteredListConfigurationValueDescriptor;
import com.recom.persistence.configuration.ConfigurationPersistenceLayer;
import com.recom.persistence.map.structure.MapLocatedStructurePersistenceLayer;
import com.recom.service.map.GameMapService;
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
    private final GameMapService gameMapService;
    @NonNull
    private final ConfigurationValueProvider configurationValueProvider;
    @NonNull
    private final ApplicationEventPublisher applicationEventPublisher;
    @NonNull
    private final ConfigurationPersistenceLayer configurationPersistenceLayer;
    @NonNull
    private final MapLocatedStructurePersistenceLayer mapStructurePersistenceLayer;

    @NonNull
    @Transactional(readOnly = false)
    public List<String> addResources(
            @NonNull final GameMap gameMap,
            @NonNull final List<String> addResourcesMatcherList,
            @NonNull final RegisteredListConfigurationValueDescriptor<String> listConfigurationValueDescriptor
    ) {
        final AddResourcesTuple resultTuple = mergeWithExistingConfigurationList(gameMap, addResourcesMatcherList, listConfigurationValueDescriptor);
        overrideListValue(gameMap, listConfigurationValueDescriptor, resultTuple.mergedListToPersist());
        applicationEventPublisher.publishEvent(new CacheResetAsyncEvent());

        return resultTuple.resourcesToAdd().stream()
                .sorted()
                .toList();
    }

    @NonNull
    private ConfigurationMapToolsService.AddResourcesTuple mergeWithExistingConfigurationList(
            @NonNull final GameMap gameMap,
            @NonNull final List<String> addResourcesMatcherList,
            @NonNull final RegisteredListConfigurationValueDescriptor<String> configurationValueDescriptor
    ) {
        final Set<String> resourcesToAdd = new HashSet<>();

        addResourcesMatcherList.stream()
                .distinct()
                .forEach(entityMatcher -> {
                    final List<String> matchedResources = gameMapService.provideGameMapMetaData(gameMap).getUtilizedResources().stream()
                            .filter(utilizedResource -> utilizedResource.toLowerCase().matches(entityMatcher.toLowerCase()))
                            .toList();
                    resourcesToAdd.addAll(matchedResources);

                    final List<String> matchedPrefabs = gameMapService.provideGameMapMetaData(gameMap).getUtilizedPrefabs().stream()
                            .filter(utilizedPrefab -> utilizedPrefab.toLowerCase().matches(entityMatcher.toLowerCase()))
                            .toList();
                    final List<String> matchedResourcesByPrefabs = mapStructurePersistenceLayer.findAllByPrefabIn(gameMap, matchedPrefabs).stream()
                            .map(MapStructureEntity::getResourceName)
                            .toList();
                    resourcesToAdd.addAll(matchedResourcesByPrefabs);

                    final List<String> matchedClasses = gameMapService.provideGameMapMetaData(gameMap).getUtilizedClasses().stream()
                            .filter(utilizedPrefab -> utilizedPrefab.toLowerCase().matches(entityMatcher.toLowerCase()))
                            .toList();
                    final List<String> matchedResourcesByClasses = mapStructurePersistenceLayer.findAllByClassIn(gameMap, matchedClasses).stream()
                            .map(MapStructureEntity::getResourceName)
                            .toList();
                    resourcesToAdd.addAll(matchedResourcesByClasses);

                    final List<String> matchedMapDescriptorTypes = gameMapService.provideGameMapMetaData(gameMap).getUtilizedClasses().stream()
                            .filter(utilizedPrefab -> utilizedPrefab.toLowerCase().matches(entityMatcher.toLowerCase()))
                            .toList();
                    final List<String> matchedResourcesByMapDescriptorTypes = mapStructurePersistenceLayer.findAllByMapDescriptorTypeIn(gameMap, matchedMapDescriptorTypes).stream()
                            .map(MapStructureEntity::getResourceName)
                            .toList();
                    resourcesToAdd.addAll(matchedResourcesByMapDescriptorTypes);
                });

        final List<String> existingClusterResources = configurationValueProvider.queryValue(gameMap, configurationValueDescriptor);

        List<String> mergedResourceList = new ArrayList<>(existingClusterResources);
        mergedResourceList.addAll(resourcesToAdd.stream().filter(Objects::nonNull).toList());
        mergedResourceList = mergedResourceList.stream()
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        return new AddResourcesTuple(resourcesToAdd.stream().filter(Objects::nonNull).collect(Collectors.toSet()), mergedResourceList);
    }

    @SneakyThrows
    public <T> void overrideListValue(
            @NonNull final GameMap gameMap,
            @NonNull final RegisteredListConfigurationValueDescriptor<T> configurationValueDescribable,
            @NonNull final List<T> mergedList
    ) {
        final Optional<Configuration> maybeConfiguration = configurationValueProvider.queryMostConcreteConfiguration(gameMap, configurationValueDescribable);

        if (isDefaultListValue.test(maybeConfiguration)) {
            maybeConfiguration.get().setValue(StaticObjectMapperProvider.provide().writeValueAsString(mergedList));

            configurationPersistenceLayer.save(maybeConfiguration.get());
        } else {
            // @TODO this is a candidate for a service ... this code is already duplicated multiple times.
            final Configuration.ConfigurationBuilder configurationBuilder = Configuration.builder()
                    .gameMap(gameMap)
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
            @NonNull final GameMap gameMap,
            @NonNull final List<String> removeResourcesMatcherList,
            @NonNull final RegisteredListConfigurationValueDescriptor<String> listConfigurationValueDescriptor
    ) {
        final RemoveResourcesTuple resultTuple = removeFromExistingConfigurationList(gameMap, removeResourcesMatcherList, listConfigurationValueDescriptor);
        overrideListValue(gameMap, listConfigurationValueDescriptor, resultTuple.mergedListToPersist());
        applicationEventPublisher.publishEvent(new CacheResetAsyncEvent());

        return resultTuple.resourcesToRemove().stream()
                .sorted()
                .toList();
    }

    @NonNull
    private ConfigurationMapToolsService.RemoveResourcesTuple removeFromExistingConfigurationList(
            @NonNull final GameMap gameMap,
            @NonNull final List<String> removeResourcesMatcherList,
            @NonNull final RegisteredListConfigurationValueDescriptor<String> configurationValueDescriptor
    ) {
        final Set<String> resourcesToRemove = new HashSet<>();
        final List<String> existingClusterResources = configurationValueProvider.queryValue(gameMap, configurationValueDescriptor);
        removeResourcesMatcherList.stream()
                .distinct()
                .forEach(entityMatcher -> {
                    final List<String> matchedEntities = existingClusterResources.stream()
                            .filter(utilizedClass -> utilizedClass.toLowerCase().matches(entityMatcher.toLowerCase()))
                            .toList();

                    resourcesToRemove.addAll(matchedEntities);
                });

        existingClusterResources.removeIf(resourcesToRemove::contains);

        return new RemoveResourcesTuple(resourcesToRemove, existingClusterResources);
    }

    private record AddResourcesTuple(Set<String> resourcesToAdd, List<String> mergedListToPersist) {
    }

    private record RemoveResourcesTuple(Set<String> resourcesToRemove, List<String> mergedListToPersist) {
    }

}
