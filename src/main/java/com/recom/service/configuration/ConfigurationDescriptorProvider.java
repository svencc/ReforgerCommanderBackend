package com.recom.service.configuration;

import com.recom.model.configuration.descriptor.BaseRegisteredConfigurationValueDescripable;
import com.recom.model.configuration.descriptor.RegisteredDoubleConfigurationValueDescriptor;
import com.recom.model.configuration.descriptor.RegisteredIntegerConfigurationValueDescriptor;
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
            .build();
    @NonNull
    public static final RegisteredIntegerConfigurationValueDescriptor MINIMUM_NUMBER_OF_POINTS_NEEDED_FOR_CLUSTER = RegisteredIntegerConfigurationValueDescriptor.builder()
            .namespace("map.clustering.convex-hull")
            .name("minimumNumberOfPointsNeededForCluster")
            .defaultValue("6")
            .build();

    @NonNull
    private final DefaultConfigurationDatabaseInitializer defaultConfigurationDatabaseInitializer;

    @PostConstruct
    public void postConstruct() {
        defaultConfigurationDatabaseInitializer.registerDefaultConfigurationProvider(this);
    }

    @Override
    public @NonNull List<BaseRegisteredConfigurationValueDescripable> provideDefaultConfigurationValues() {
        return List.of(
                ConfigurationDescriptorProvider.EPSILON_MAXIMUM_RADIUS_OF_THE_NEIGHBORHOOD,
                ConfigurationDescriptorProvider.MINIMUM_NUMBER_OF_POINTS_NEEDED_FOR_CLUSTER
        );
    }

}
