package com.recom.commander.service.gateway;

import com.recom.commander.exception.RequestLogger;
import com.recom.commander.exception.exceptions.http.HttpErrorException;
import com.recom.commander.exception.exceptions.http.HttpNoConnectionException;
import com.recom.commander.model.Provideable;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.time.Instant;

@RequiredArgsConstructor
public abstract class Gateway<REQUEST, RESPONSE> {

    @NonNull
    private final Provideable<RestClient> restClientProvideable;
    @NonNull
    private final RequestLogger requestLogger;

    @NonNull
    private static <RESPONSE> ParameterizedTypeReference<RESPONSE> typeReference() {
        return new ParameterizedTypeReference<RESPONSE>() {
        };
    }

    @NonNull
    protected ResponseEntity<RESPONSE> sendWithResponseEntity(
            @NonNull final REQUEST nullableRequestBody,
            @NonNull final Class<RESPONSE> responseClazz
    ) {
        return retrieve(nullableRequestBody, responseClazz);
    }

    @NonNull
    protected ResponseEntity<RESPONSE> retrieve(
            @Nullable final REQUEST nullableRequestBody,
            @NonNull final Class<RESPONSE> responseClazz
    ) throws HttpErrorException {
        try {
            final Instant started = Instant.now();
            return specifyRequest(restClientProvideable.provide(), nullableRequestBody)
                    .retrieve()
                    .onStatus(httpStatus -> !httpStatus.is2xxSuccessful(), (request, response) -> requestLogger.logRequestInErrorCase(request, response, nullableRequestBody))
                    //.onStatus(HttpStatusCode::is2xxSuccessful, (request, response) -> requestLogger.profileRequest(request, response, started))
                    .<RESPONSE>toEntity(responseClazz);
        } catch (final ResourceAccessException e) {
            throw new HttpNoConnectionException(getClass(), e);
        }
    }

    @NonNull
    protected abstract RestClient.RequestBodySpec specifyRequest(
            @NonNull final RestClient restClient,
            @Nullable final REQUEST genericRequest
    );

    /*
    @NonNull
    protected Optional<RESPONSE> sendWithMaybeResponse(@NonNull final REQUEST nullableRequestBody) {
        return Optional.ofNullable(retrieve(nullableRequestBody).getBody());
    }
     */

    @NonNull
    protected RESPONSE sendWithResponse(
            @NonNull final REQUEST nullableRequestBody,
            @NonNull final Class<RESPONSE> responseClazz
    ) {
        final ResponseEntity<RESPONSE> retrieve = retrieve(nullableRequestBody, responseClazz);
        return retrieve.getBody();
    }

    @NonNull
    protected void send(
            @NonNull final REQUEST nullableRequestBody,
            @NonNull final Class<RESPONSE> responseClazz
    ) {
        retrieve(nullableRequestBody, responseClazz).getBody();
    }

    /*
    @NonNull
    protected void send() {
        retrieve(null);
    }

    @NonNull
    protected Optional<RESPONSE> sendWithMaybeResponse() {
        return Optional.ofNullable(retrieve(null).getBody());
    }

    @NonNull
    protected RESPONSE sendWithResponse() {
        return retrieve(null).getBody();
    }

    @NonNull
    protected ResponseEntity<RESPONSE> sendWithResponseEntity() {
        return retrieve(null);
    }
     */

}
