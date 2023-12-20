package com.recom.commander.configuration;

import com.recom.commander.property.RendererProperties;
import com.recom.commander.property.RestClientProperties;
import com.recom.commander.property.SpringApplicationProperties;
import com.recom.commander.property.gateway.AuthenticationGatewayProperties;
import com.recom.commander.property.gateway.MapTopographyGatewayProperties;
import com.recom.commander.property.gateway.RECOMHostProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        SpringApplicationProperties.class,
        RendererProperties.class,
        RestClientProperties.class,
        RECOMHostProperties.class,
        MapTopographyGatewayProperties.class,
        AuthenticationGatewayProperties.class
})
public class PropertiesConfiguration {

}
