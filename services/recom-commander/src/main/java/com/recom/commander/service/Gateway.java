package com.recom.commander.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recom.commander.exception.RequestLogger;
import com.recom.commander.exception.exceptions.http.HttpErrorException;
import com.recom.commander.exception.exceptions.http.HttpNoConnectionException;
import com.recom.commander.model.Provideable;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.time.Instant;

@RequiredArgsConstructor
public abstract class Gateway<REQUEST, RESPONSE> {

    @NonNull
    private final RequestLogger requestLogger;
    @NonNull
    private final Provideable<RestClient> restClientProvideable;
    @NonNull
    private final Class<RESPONSE> responseClazz;
    @NonNull
    private final ObjectMapper objectMapper;

    @NonNull
    private static <RESPONSE> ParameterizedTypeReference<RESPONSE> typeReference() {
        return new ParameterizedTypeReference<RESPONSE>() {
        };
    }

    @NonNull
    protected ResponseEntity<RESPONSE> sendWithResponseEntity() {
        return exchange(null);
    }

    @NonNull
    protected ResponseEntity<RESPONSE> sendWithResponseEntity(@NonNull final REQUEST requestBody) {
        return exchange(requestBody);
    }

    @NonNull
    protected ResponseEntity<RESPONSE> exchange(@Nullable final REQUEST nullableRequestBody) throws HttpErrorException {
        try {
            final Instant started = Instant.now();
            final ResponseEntity<RESPONSE> entity = specifyRequest(restClientProvideable.provide(), nullableRequestBody)
                    .exchange((request, response) -> {
                        if (response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(200))) {
                            if (response.getBody() != null) {
                                try {
                                    final RESPONSE responseBody = objectMapper.readValue(response.getBody(), responseClazz);
                                    requestLogger.profileRequest(request, response, nullableRequestBody, responseBody, started);
                                    return ResponseEntity.ok(responseBody);
                                } catch (final Exception e) {
                                    throw new HttpErrorException(e.getMessage(), e);
                                }
                            } else {
                                return ResponseEntity.ok(null);
                            }
                        } else {
                            requestLogger.logRequestInErrorCase(request, response, nullableRequestBody, null);
                            return ResponseEntity.ok(null);
                        }
                    });
            return entity;
        } catch (final ResourceAccessException e) {
            throw new HttpNoConnectionException(getClass(), e);
        }
    }

    @NonNull
    protected abstract RestClient.RequestHeadersSpec<?> specifyRequest(
            @NonNull final RestClient restClient,
            @Nullable final REQUEST genericRequest
    );

    @NonNull
    protected RESPONSE sendWithResponse() {
        final ResponseEntity<RESPONSE> retrieve = exchange(null);
        return retrieve.getBody();
    }

    @NonNull
    protected RESPONSE sendWithResponse(@NonNull final REQUEST nullableRequestBody) {
        final ResponseEntity<RESPONSE> retrieve = exchange(nullableRequestBody);
        return retrieve.getBody();
    }

//    @NonNull
//    protected void send(@NonNull final REQUEST nullableRequestBody) {
//        exchange(nullableRequestBody).getBody();
//    }

}
