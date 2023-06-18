package com.recom.service.map.cluster;

import com.recom.model.configuration.descriptor.RegisteredDoubleConfigurationValueDescriptor;
import com.recom.model.configuration.descriptor.RegisteredIntegerConfigurationValueDescriptor;

public class ClusterConfigurationDescriptors {

    public static final RegisteredDoubleConfigurationValueDescriptor EPSILON_MAXIMUM_RADIUS_OF_THE_NEIGHBORHOOD = RegisteredDoubleConfigurationValueDescriptor.builder()
            .namespace("map.clustering.convex-hull")
            .name("epsilonMaximumRadiusOfTheNeighborhood")
            .defaultValue("250")
            .build();

    public static final RegisteredIntegerConfigurationValueDescriptor MINIMUM_NUMBER_OF_POINTS_NEEDED_FOR_CLUSTER = RegisteredIntegerConfigurationValueDescriptor.builder()
            .namespace("map.clustering.convex-hull")
            .name("minimumNumberOfPointsNeededForCluster")
            .defaultValue("6")
            .build();

}
