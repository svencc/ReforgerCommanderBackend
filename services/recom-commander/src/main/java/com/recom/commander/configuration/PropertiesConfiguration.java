package com.recom.commander.configuration;

import com.recom.commander.property.RestClientProperties;
import com.recom.commander.property.SpringApplicationProperties;
import com.recom.commander.property.gateway.AuthenticationGatewayProperties;
import com.recom.commander.property.gateway.MapOverviewGatewayProperties;
import com.recom.commander.property.gateway.MapTopographyDataGatewayProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        SpringApplicationProperties.class,
        RestClientProperties.class,
        AuthenticationGatewayProperties.class,
        MapOverviewGatewayProperties.class,
        MapTopographyDataGatewayProperties.class,
})
public class PropertiesConfiguration {

}
