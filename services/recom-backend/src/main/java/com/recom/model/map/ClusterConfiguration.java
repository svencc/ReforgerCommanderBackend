package com.recom.model.map;

import com.recom.model.configuration.descriptor.RegisteredDoubleConfigurationValueDescriptor;
import com.recom.model.configuration.descriptor.RegisteredIntegerConfigurationValueDescriptor;
import com.recom.model.configuration.descriptor.RegisteredListConfigurationValueDescriptor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class ClusterConfiguration {

    @NonNull
    private final RegisteredListConfigurationValueDescriptor<String> clusteringResourcesListDescriptor;

    @NonNull
    private final RegisteredDoubleConfigurationValueDescriptor dbscanClusteringEpsilonMaximumRadiusOfTheNeighborhoodDescriptor;

    @NonNull
    private final RegisteredIntegerConfigurationValueDescriptor dbscanClusteringVillageMinimumPointsDescriptor;

}
