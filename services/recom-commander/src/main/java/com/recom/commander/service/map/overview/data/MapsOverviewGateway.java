package com.recom.commander.service.map.overview.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recom.commander.exception.RequestLogger;
import com.recom.commander.property.gateway.MapOverviewGatewayProperties;
import com.recom.commander.property.restclient.RECOMRestClientProvider;
import com.recom.commander.service.Gateway;
import com.recom.dto.map.MapOverviewDto;
import lombok.NonNull;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
class MapsOverviewGateway extends Gateway<Void, MapOverviewDto> {

    @NonNull
    private final MapOverviewGatewayProperties mapOverviewGatewayProperties;


    public MapsOverviewGateway(
            @NonNull final RequestLogger requestLogger,
            @NonNull final RECOMRestClientProvider restClientProvider,
            @NonNull final MapOverviewGatewayProperties mapOverviewGatewayProperties,
            @NonNull final ObjectMapper objectMapper

    ) {
        super(requestLogger, restClientProvider, MapOverviewDto.class, objectMapper);
        this.mapOverviewGatewayProperties = mapOverviewGatewayProperties;
    }

    @Override
    protected RestClient.RequestHeadersSpec<?> specifyRequest(
            @NonNull final RestClient restClient,
            @Nullable final Void __
    ) {
        return restClient
                .get()
                .uri(mapOverviewGatewayProperties.getEndpoint())
                .accept(MediaType.APPLICATION_JSON);
    }

    @NonNull
    MapOverviewDto provideMapOverview() {
        return super.sendWithResponse();
    }

}
