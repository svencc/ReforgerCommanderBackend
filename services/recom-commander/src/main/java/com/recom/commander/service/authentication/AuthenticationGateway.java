package com.recom.commander.service.authentication;

import com.recom.commander.exception.RequestLogger;
import com.recom.commander.exception.exceptions.http.HttpErrorException;
import com.recom.commander.property.gateway.AuthenticationGatewayProperties;
import com.recom.commander.property.restclient.RECOMRestClientProvider;
import com.recom.commander.property.user.AuthenticationProperties;
import com.recom.dto.authentication.AuthenticationRequestDto;
import com.recom.dto.authentication.AuthenticationResponseDto;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class AuthenticationGateway {

    @NonNull
    private final RECOMRestClientProvider recomRestClientProvider;
    @NonNull
    private final AuthenticationGatewayProperties authenticationGatewayProperties;
    @NonNull
    private final AuthenticationProperties authenticationProperties;
    @NonNull
    private final RequestLogger requestLogger;


    @NonNull
    public AuthenticationResponseDto authenticate() throws HttpErrorException, ResourceAccessException {
        final Instant started = Instant.now();
        final ResponseEntity<AuthenticationResponseDto> responseEntity = recomRestClientProvider.provide()
                .post()
                .uri(authenticationGatewayProperties.getEndpoint())
                .accept(MediaType.APPLICATION_JSON)
                .body(AuthenticationRequestDto.builder()
                        .accountUUID(authenticationProperties.getAccountUUID())
                        .accessKey(authenticationProperties.getAccessKey())
                        .build()
                )
                .retrieve()
                .onStatus(httpStatus -> !httpStatus.is2xxSuccessful(), requestLogger::logRequestInErrorCase)
                .onStatus(HttpStatusCode::is2xxSuccessful, (request, response) -> requestLogger.profileRequest(request, response, started))
                .toEntity(AuthenticationResponseDto.class);

        return responseEntity.getBody();
    }

}
