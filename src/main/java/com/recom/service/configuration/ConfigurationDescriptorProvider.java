package com.recom.service.configuration;

import com.recom.model.configuration.ConfigurationType;
import com.recom.model.configuration.descriptor.BaseRegisteredConfigurationValueDescribable;
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
            .build();
    @NonNull
    public static final RegisteredIntegerConfigurationValueDescriptor MINIMUM_NUMBER_OF_POINTS_NEEDED_FOR_CLUSTER = RegisteredIntegerConfigurationValueDescriptor.builder()
            .namespace("map.clustering.convex-hull")
            .name("minimumNumberOfPointsNeededForCluster")
            .defaultValue("6")
            .build();
    @NonNull
    public static final RegisteredListConfigurationValueDescriptor<String> CLUSTERING_VILLAGE_RESOURCES_LIST = RegisteredListConfigurationValueDescriptor.<String>builder()
            .namespace("map.clustering.resources")
            .name("village")
            .typeHint(ConfigurationType.STRING)
            .listValue(List.of(
                    "{07AECD766DD2ED77}Assets/Structures/Houses/Village/House_Village_E_1I04/House_Village_E_1I04t.xob",
                    "{11B16ABA6777AF59}Assets/Structures/Houses/Village/House_Village_E_1I02/House_Village_E_1I02.xob",
                    "{1BD0E4D4B505A9CB}Assets/Structures/Houses/Village/House_Village_E_1I08/House_Village_E_1I08t.xob",
                    "{4F4F368363DD7883}Assets/Structures/Houses/Village/House_Village_E_1I03/House_Village_E_1I03.xob",
                    "{6809D93C24620812}Assets/Structures/Houses/Village/House_Village_E_1L01/House_Village_E_1L01.xob",
                    "{7A2C31FD967DA9FF}Assets/Structures/Houses/Village/House_Mountain_E_1I01/House_Mountain_E_1I01.xob",
                    "{89D6E2EB1CEEBD2C}Assets/Structures/Houses/Village/House_Village_E_1I04/House_Village_E_1I04sr.xob",
                    "{92C1324CEB212168}Assets/Structures/Houses/Village/House_Village_E_1I05/House_Village_E_1I05t.xob",
                    "{9422C38E826E8DF4}Assets/Structures/Houses/Village/HouseAddon/HouseAddon_Shed_E_01.xob",
                    "{9506C81D8DA79BAB}Assets/Structures/Houses/Village/House_Village_E_1I05/House_Village_E_1I05s.xob",
                    "{9ACF82C6230EE1C7}Assets/Structures/Houses/Village/House_Village_E_1L03/House_Village_E_1L03t.xob",
                    "{A4743FC733F23962}Assets/Structures/Houses/Village/HouseAddon/HouseAddon_Garage_E_01.xob",
                    "{ABEDE231699E9DEE}Assets/Structures/Houses/Village/HouseAddon/HouseAddon_Workshop_E_01.xob",
                    "{B148FBA4228D13EB}Assets/Structures/Houses/Village/House_Village_E_1I04/House_Village_E_1I04sf.xob",
                    "{B608F5042424A544}Assets/Structures/Houses/Village/House_Village_E_1L02/House_Village_E_1L02t.xob",
                    "{F2B38EF16A88D737}Assets/Structures/Houses/Village/House_Village_E_1I01/House_Village_E_1I01.xob",
                    "{FC6B11986781498F}Assets/Structures/Houses/Village/House_Mountain_E_1I01/House_Mountain_E_1I01_short.xob",
                    "{FFDEE5B5236275EE}Assets/Structures/Houses/Village/House_Village_E_1I06/House_Village_E_1I06.xob"
            ))
            .build();

    @NonNull
    private final DefaultConfigurationDatabaseInitializer defaultConfigurationDatabaseInitializer;

    @PostConstruct
    public void postConstruct() {
        defaultConfigurationDatabaseInitializer.registerDefaultConfigurationProvider(this);
    }

    @Override
    public @NonNull List<BaseRegisteredConfigurationValueDescribable> provideDefaultConfigurationValues() {
        return List.of(
                ConfigurationDescriptorProvider.EPSILON_MAXIMUM_RADIUS_OF_THE_NEIGHBORHOOD,
                ConfigurationDescriptorProvider.MINIMUM_NUMBER_OF_POINTS_NEEDED_FOR_CLUSTER,
                ConfigurationDescriptorProvider.CLUSTERING_VILLAGE_RESOURCES_LIST
        );
    }

}
