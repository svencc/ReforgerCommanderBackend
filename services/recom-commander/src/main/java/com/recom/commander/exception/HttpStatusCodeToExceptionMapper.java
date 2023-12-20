package com.recom.commander.exception;

import com.recom.commander.exception.exceptions.http.*;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

import static org.springframework.http.HttpStatus.*;

@UtilityClass
public class HttpStatusCodeToExceptionMapper {


    public HttpErrorException mapStatusCodeToException(@NonNull final ClientHttpResponse response) {
        HttpStatusCode statusCode = null;
        String statusCodeString = null;
        String responseBodyString = "<null>";
        try {
            statusCode = response.getStatusCode();
            statusCodeString = response.getStatusCode().toString();
            responseBodyString = new String(response.getBody().readAllBytes());
        } catch (IOException __) {
        }

        return switch (statusCode) {
            case BAD_REQUEST ->
                    new HttpBadRequestException(String.format("Request failed with status %s", statusCodeString), responseBodyString);
            case UNAUTHORIZED ->
                    new HttpUnauthorizedException(String.format("Request failed with status %s", statusCodeString), responseBodyString);
            case FORBIDDEN ->
                    new HttpForbiddenException(String.format("Request failed with status %s", statusCodeString), responseBodyString);
            case NOT_FOUND ->
                    new HttpNotFoundException(String.format("Request failed with status %s", statusCodeString), responseBodyString);
            case METHOD_NOT_ALLOWED ->
                    new HttpMethodNotAllowedException(String.format("Request failed with status %s", statusCodeString), responseBodyString);
            case NOT_ACCEPTABLE ->
                    new HttpNotAcceptableException(String.format("Request failed with status %s", statusCodeString), responseBodyString);
            case UNSUPPORTED_MEDIA_TYPE ->
                    new HttpUnsupportedMediaTypeException(String.format("Request failed with status %s", statusCodeString), responseBodyString);
            case TOO_MANY_REQUESTS ->
                    new HttpTooManyRequestsException(String.format("Request failed with status %s", statusCodeString), responseBodyString);
            case INTERNAL_SERVER_ERROR ->
                    new HttpInternalServerErrorException(String.format("Request failed with status %s", statusCodeString), responseBodyString);
            case SERVICE_UNAVAILABLE ->
                    new HttpServiceUnavailableException(String.format("Request failed with status %s", statusCodeString), responseBodyString);
            default ->
                    new HttpErrorException(String.format("Request failed with status %s", statusCodeString), responseBodyString);
        };
    }

}
