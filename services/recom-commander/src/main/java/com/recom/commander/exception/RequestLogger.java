package com.recom.commander.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestLogger {

    @NonNull
    private final ObjectMapper objectMapper;
    @NonNull
    private final AsyncTaskExecutor asyncTaskExecutor;


    public void logRequestInErrorCase(
            @NonNull final HttpRequest request,
            @NonNull final ClientHttpResponse response,
            @Nullable final Object nullableRequestBody,
            @Nullable final Object nullableResponseBody
    ) {
        asyncTaskExecutor.submit(() -> {
            final String message = prepareLogMessage(request, response, nullableRequestBody, nullableResponseBody);
            log.error(message + "\n");
            throw HttpStatusCodeToExceptionMapper.mapStatusCodeToException(response);
        });
    }

    @NonNull
    private String prepareLogMessage(
            @NonNull final HttpRequest request,
            @NonNull final ClientHttpResponse response,
            @Nullable final Object nullableRequestBody,
            @Nullable final Object nullableResponseBody
    ) {
        final String requestHeaders = request.getHeaders().entrySet().stream()
                .map(entry -> String.format("| |- %s: %s", entry.getKey().trim(), entry.getValue().toString().trim()))
                .reduce((a, b) -> String.format("%s\n%s", a, b))
                .orElse(" <empty requestHeaders>");

        String requestBody = "<null>";
        if (nullableRequestBody != null) {
            try {
                requestBody = objectMapper.writeValueAsString(nullableRequestBody);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        final String responseHeaders = response.getHeaders().entrySet().stream()
                .map(entry -> String.format("| |- %s: %s", entry.getKey().trim(), entry.getValue().toString().trim()))
                .reduce((a, b) -> String.format("%s\n%s", a, b))
                .orElse(" <empty responseHeaders>");

        String responseStatusCodeString;
        try {
            responseStatusCodeString = String.valueOf(response.getStatusCode().value());
        } catch (final Throwable t) {
            responseStatusCodeString = "<status code>";
        }

        String responseStatusText;
        try {
            responseStatusText = response.getStatusCode().toString();
        } catch (final Throwable t) {
            responseStatusText = "<status text>";
        }

        String responseBody = "<null>";
        try {
            responseBody = objectMapper.writeValueAsString(nullableResponseBody);
        } catch (final Throwable __) {
        }

        return String.format("""
                                                
                        +---- -------- -------- -------[ REQUEST LOGGER ]------- -------- -------- -----
                        |
                        *=====> [REQUEST]: %s -> %s
                        |\\
                        | +---> Headers:
                        %s
                        |
                         \\
                          +---> Body:
                        %s
                                                
                        +---- -------- -------- -------[ RESPONSE LOGGER ]------- -------- -------- ----
                        |
                        *=====> [RESPONSE]: %s -> %s
                        |\\
                        | +---> Headers:
                        %s
                        |
                         \\
                          +---> Body:
                        %s          
                                                              
                        --------------------------------------------------------------------------------
                        """.stripIndent(),

                request.getMethod(), request.getURI(),
                requestHeaders, requestBody,

                responseStatusCodeString, responseStatusText,
                responseHeaders,
                responseBody
        );
    }

    public void profileRequest(
            @NonNull final HttpRequest request,
            @NonNull final ClientHttpResponse response,
            @NonNull final Object nullableRequestBody,
            @NonNull final Object nullableResponseBody,
            @NonNull final Instant start
    ) {
        asyncTaskExecutor.submit(() -> log.debug(prepareLogMessage(request, response, nullableRequestBody, nullableResponseBody) + prepareDurationInfo(start)));
    }


    @NonNull
    private String prepareDurationInfo(Instant start) {
        return String.format("""
                        | (i) Duration: %s ms
                        --------------------------------------------------------------------------------
                                                
                        """.stripIndent(),
                Instant.now().toEpochMilli() - start.toEpochMilli()
        );
    }

}
