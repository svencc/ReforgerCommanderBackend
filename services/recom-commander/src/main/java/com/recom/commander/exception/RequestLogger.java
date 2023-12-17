package com.recom.commander.exception;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@Service
public class RequestLogger {

    @NonNull
    public void logRequestInErrorCase(
            @NonNull final HttpRequest request,
            @NonNull final ClientHttpResponse response
    ) {
        final String message = prepareLogMessage(request, response);
        log.error(message + "\n");
        mapStatusCodeToException(response);
    }

    private void mapStatusCodeToException(@NonNull final ClientHttpResponse response) {
        HttpStatusCode statusCode = null;
        String statusCodeString = null;
        String responseBodyString = "<null>";
        try {
            statusCode = response.getStatusCode();
            statusCodeString = response.getStatusCode().toString();
            responseBodyString = new String(response.getBody().readAllBytes());
        } catch (IOException __) {
        }
        switch (statusCode) {
            case BAD_REQUEST:
                throw new HttpBadRequestException(String.format("Request failed with status %s", statusCodeString), responseBodyString);
            case UNAUTHORIZED:
                throw new HttpUnauthorizedException(String.format("Request failed with status %s", statusCodeString), responseBodyString);
            case FORBIDDEN:
                throw new HttpForbiddenException(String.format("Request failed with status %s", statusCodeString), responseBodyString);
            case NOT_FOUND:
                throw new HttpNotFoundException(String.format("Request failed with status %s", statusCodeString), responseBodyString);
            case METHOD_NOT_ALLOWED:
                throw new HttpMethodNotAllowedException(String.format("Request failed with status %s", statusCodeString), responseBodyString);
            case NOT_ACCEPTABLE:
                throw new HttpNotAcceptableException(String.format("Request failed with status %s", statusCodeString), responseBodyString);
            case UNSUPPORTED_MEDIA_TYPE:
                throw new HttpUnsupportedMediaTypeException(String.format("Request failed with status %s", statusCodeString), responseBodyString);
            case TOO_MANY_REQUESTS:
                throw new HttpTooManyRequestsException(String.format("Request failed with status %s", statusCodeString), responseBodyString);
            case INTERNAL_SERVER_ERROR:
                throw new HttpInternalServerErrorException(String.format("Request failed with status %s", statusCodeString), responseBodyString);
            case SERVICE_UNAVAILABLE:
                throw new HttpServiceUnavailableException(String.format("Request failed with status %s", statusCodeString), responseBodyString);
            default:
                throw new HttpErrorException(String.format("Request failed with status %s", statusCodeString), responseBodyString);
        }
    }

    private static String prepareLogMessage(HttpRequest request, ClientHttpResponse response) {
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
        } catch (final Throwable t) {
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

    private static String prepareDurationInfo(Instant start) {
        return String.format("""
                        | (i) Duration: %s
                        --------------------------------------------------------------------------------
                                                
                        """.stripIndent(),
                Instant.now().toEpochMilli() - start.toEpochMilli()
        );
    }
}
