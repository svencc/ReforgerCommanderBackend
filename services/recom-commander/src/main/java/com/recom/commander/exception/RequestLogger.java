package com.recom.commander.exception;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
public class RequestLogger {

    public void logRequestInErrorCase(
            @NonNull final HttpRequest request,
            @NonNull final ClientHttpResponse response
    ) {
        final String message = prepareLogMessage(request, response);
        log.error(message + "\n");
        throw HttpStatusCodeToExceptionMapper.mapStatusCodeToException(response);
    }

    @NonNull
    private String prepareLogMessage(HttpRequest request, ClientHttpResponse response) {
        final String requestHeaders = request.getHeaders().entrySet().stream()
                .map(entry -> String.format("| |- %s: %s", entry.getKey().trim(), entry.getValue().toString().trim()))
                .reduce((a, b) -> String.format("%s\n%s", a, b))
                .orElse(" empty requestHeaders");
        final String responseHeaders = response.getHeaders().entrySet().stream()
                .map(entry -> String.format("| |- %s: %s", entry.getKey().trim(), entry.getValue().toString().trim()))
                .reduce((a, b) -> String.format("%s\n%s", a, b))
                .orElse(" empty responseHeaders");

        String responseStatusCodeString;
        try {
            responseStatusCodeString = response.getStatusCode().toString();
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
            responseBody = new String(response.getBody().readAllBytes());
        } catch (final Throwable __) {
        }

        return String.format("""
                                                
                        +---- -------- -------- -------[ REQUEST LOGGER ]------- -------- -------- -----
                        |
                        |=====> REQUEST: %s -> %s
                        |\\
                        | +---> Headers:
                        %s
                        |
                        |=====> RESPONSE: %s -> %s
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
                requestHeaders,

                responseStatusCodeString, responseStatusText,
                responseHeaders,
                responseBody
        );
    }

    public void profileRequest(
            @NonNull final HttpRequest request,
            @NonNull final ClientHttpResponse response,
            @NonNull final Instant start
    ) {
        log.debug(prepareLogMessage(request, response) + prepareDurationInfo(start));
    }

    @NonNull
    private String prepareDurationInfo(Instant start) {
        return String.format("""
                        | (i) Duration: %s
                        --------------------------------------------------------------------------------
                                                
                        """.stripIndent(),
                Instant.now().toEpochMilli() - start.toEpochMilli()
        );
    }

}
