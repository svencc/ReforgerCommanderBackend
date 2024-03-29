package com.recom.commander.service.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recom.commander.exception.RequestLogger;
import com.recom.commander.property.gateway.AuthenticationGatewayProperties;
import com.recom.commander.property.restclient.RECOMUnauthenticatedRestClientProvider;
import com.recom.commander.service.Gateway;
import com.recom.dto.authentication.AuthenticationRequestDto;
import com.recom.dto.authentication.AuthenticationResponseDto;
import lombok.NonNull;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
class AuthenticationGateway extends Gateway<AuthenticationRequestDto, AuthenticationResponseDto> {

    @NonNull
    private final AuthenticationGatewayProperties authenticationGatewayProperties;


    public AuthenticationGateway(
            @NonNull final RequestLogger requestLogger,
            @NonNull final RECOMUnauthenticatedRestClientProvider restClientProvider,
            @NonNull final AuthenticationGatewayProperties authenticationGatewayProperties,
            @NonNull final ObjectMapper objectMapper

    ) {
        super(requestLogger, restClientProvider, AuthenticationResponseDto.class, objectMapper);
        this.authenticationGatewayProperties = authenticationGatewayProperties;
    }

    @Override
    protected RestClient.RequestHeadersSpec<?> specifyRequest(
            @NonNull final RestClient restClient,
            @NonNull final AuthenticationRequestDto authenticationRequest
    ) {
        return restClient
                .post()
                .uri(authenticationGatewayProperties.getEndpoint())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(authenticationRequest);
    }

    @NonNull
    public AuthenticationResponseDto authenticate(@NonNull final AuthenticationRequestDto authenticationRequestDto) {
        return super.sendWithResponse(authenticationRequestDto);
    }

}
