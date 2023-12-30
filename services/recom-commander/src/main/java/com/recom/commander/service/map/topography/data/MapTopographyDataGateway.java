package com.recom.commander.service.map.topography.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recom.commander.exception.RequestLogger;
import com.recom.commander.property.gateway.MapTopographyDataGatewayProperties;
import com.recom.commander.property.restclient.RECOMRestClientProvider;
import com.recom.commander.service.Gateway;
import com.recom.dto.map.topography.HeightMapDescriptorDto;
import com.recom.dto.map.topography.MapTopographyRequestDto;
import lombok.NonNull;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
class MapTopographyDataGateway extends Gateway<MapTopographyRequestDto, HeightMapDescriptorDto> {

    @NonNull
    private final MapTopographyDataGatewayProperties mapTopographyDataGatewayProperties;


    public MapTopographyDataGateway(
            @NonNull final RequestLogger requestLogger,
            @NonNull final RECOMRestClientProvider restClientProvider,
            @NonNull final MapTopographyDataGatewayProperties mapTopographyDataGatewayProperties,
            @NonNull final ObjectMapper objectMapper
    ) {
        super(requestLogger, restClientProvider, HeightMapDescriptorDto.class, objectMapper);
        this.mapTopographyDataGatewayProperties = mapTopographyDataGatewayProperties;
    }

    @Override
    protected RestClient.RequestBodySpec specifyRequest(
            @NonNull final RestClient restClient,
            @NonNull final MapTopographyRequestDto mapTopographyRequestDto
    ) {
        return restClient
                .post()
                .uri(mapTopographyDataGatewayProperties.getEndpoint())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(mapTopographyRequestDto);
    }

    @NonNull
    HeightMapDescriptorDto provideMapTopographyData(@NonNull final MapTopographyRequestDto mapTopographyRequest) {
        return super.sendWithResponse(mapTopographyRequest);
    }

}
