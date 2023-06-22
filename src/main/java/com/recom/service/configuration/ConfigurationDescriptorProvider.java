package com.recom.service.configuration;

import com.recom.model.configuration.ConfigurationType;
import com.recom.model.configuration.descriptor.BaseRegisteredConfigurationValueDescriptable;
import com.recom.model.configuration.descriptor.RegisteredDoubleConfigurationValueDescriptor;
import com.recom.model.configuration.descriptor.RegisteredIntegerConfigurationValueDescriptor;
import com.recom.model.configuration.descriptor.RegisteredListConfigurationValueDescriptor;
import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConfigurationDescriptorProvider implements DefaultConfigurationProvidable {

    @NonNull
    public static final RegisteredDoubleConfigurationValueDescriptor EPSILON_MAXIMUM_RADIUS_OF_THE_NEIGHBORHOOD = RegisteredDoubleConfigurationValueDescriptor.builder()
            .namespace("map.clustering.convex-hull")
            .name("epsilonMaximumRadiusOfTheNeighborhood")
            .defaultValue("250")
            .enabled(true)
            .build();
    @NonNull
    public static final RegisteredIntegerConfigurationValueDescriptor MINIMUM_NUMBER_OF_POINTS_NEEDED_FOR_CLUSTER = RegisteredIntegerConfigurationValueDescriptor.builder()
            .namespace("map.clustering.convex-hull")
            .name("minimumNumberOfPointsNeededForCluster")
            .defaultValue("6")
            .enabled(true)
            .build();
    @NonNull
    public static final RegisteredListConfigurationValueDescriptor<String> TEST_JSON_LIST = RegisteredListConfigurationValueDescriptor.<String>builder()
            .namespace("map.clustering.test-list")
            .name("testJsonList")
            .typeHint(ConfigurationType.STRING)
            .listValue(List.of("test1", "test2"))
            .enabled(true)
            .build();
    @NonNull
    private final ConfigurationValueProvider configurationValueProviderTest;
    @NonNull
    private final DefaultConfigurationDatabaseInitializer defaultConfigurationDatabaseInitializer;

    @PostConstruct
    public void postConstruct() {
        defaultConfigurationDatabaseInitializer.registerDefaultConfigurationProvider(this);
    }

    @Override
    public @NonNull List<BaseRegisteredConfigurationValueDescriptable> provideDefaultConfigurationValues() {
        return List.of(
                ConfigurationDescriptorProvider.EPSILON_MAXIMUM_RADIUS_OF_THE_NEIGHBORHOOD,
                ConfigurationDescriptorProvider.MINIMUM_NUMBER_OF_POINTS_NEEDED_FOR_CLUSTER,
                ConfigurationDescriptorProvider.TEST_JSON_LIST
        );
    }

}
