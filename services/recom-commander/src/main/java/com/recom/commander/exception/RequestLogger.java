package com.recom.commander.exception;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;

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
        final String requestHeaders = request.getHeaders().entrySet().stream()
                .map(entry -> String.format("%s: %s", entry.getKey(), entry.getValue()))
                .reduce((a, b) -> String.format(" %s\n%s", a, b))
                .orElse("empty requestHeaders");
        final String responseHeaders = response.getHeaders().entrySet().stream()
                .map(entry -> String.format("%s: %s", entry.getKey(), entry.getValue()))
                .reduce((a, b) -> String.format(" %s\n%s", a, b))
                .orElse("empty responseHeaders");

        HttpStatusCode responseStatusCode = null;
        String responseStatusCodeString;
        try {
            responseStatusCode = response.getStatusCode();
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

        String responseBody = null;
        try {
            responseBody = new String(response.getBody().readAllBytes());
        } catch (final Throwable t) {
        }

        log.error("\n\n--------------------------------");
        log.error("""
                        #REQUEST:
                         %s -> %s
                        Headers:
                        %s
                                  
                        # RESPONSE:
                         %s -> %s
                        +Headers:
                         %s
                        +Body:
                         %s
                        """.stripIndent(),

                request.getMethod(), request.getURI(),
                requestHeaders,

                responseStatusCodeString, responseStatusText,
                responseHeaders,
                responseBody
        );
        log.error("--------------------------------\n\n");

        switch (responseStatusCode) {
            case BAD_REQUEST:
                throw new HttpBadRequestException(String.format("Request failed with status %s", responseStatusCodeString), responseBody);
            case UNAUTHORIZED:
                throw new HttpUnauthorizedException(String.format("Request failed with status %s", responseStatusCodeString), responseBody);
            case FORBIDDEN:
                throw new HttpForbiddenException(String.format("Request failed with status %s", responseStatusCodeString), responseBody);
            case NOT_FOUND:
                throw new HttpNotFoundException(String.format("Request failed with status %s", responseStatusCodeString), responseBody);
            case METHOD_NOT_ALLOWED:
                throw new HttpMethodNotAllowedException(String.format("Request failed with status %s", responseStatusCodeString), responseBody);
            case NOT_ACCEPTABLE:
                throw new HttpNotAcceptableException(String.format("Request failed with status %s", responseStatusCodeString), responseBody);
            case UNSUPPORTED_MEDIA_TYPE:
                throw new HttpUnsupportedMediaTypeException(String.format("Request failed with status %s", responseStatusCodeString), responseBody);
            case TOO_MANY_REQUESTS:
                throw new HttpTooManyRequestsException(String.format("Request failed with status %s", responseStatusCodeString), responseBody);
            case INTERNAL_SERVER_ERROR:
                throw new HttpInternalServerErrorException(String.format("Request failed with status %s", responseStatusCodeString), responseBody);
            case SERVICE_UNAVAILABLE:
                throw new HttpServiceUnavailableException(String.format("Request failed with status %s", responseStatusCodeString), responseBody);
            default:
                throw new HttpErrorException(String.format("Request failed with status %s", responseStatusCodeString), responseBody);
        }
    }

    public void profileRequest(@NonNull final Instant start) {
        log.info("Duration: " + (Instant.now().toEpochMilli() - start.toEpochMilli()));
    }
}
