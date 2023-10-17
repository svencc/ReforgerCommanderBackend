package com.recom.exception;

import com.recom.dto.ConstraintViolationEntryDto;
import com.recom.dto.ServerMessageDto;
import jakarta.validation.ConstraintViolationException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.Optional;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // VALIDATION EXCEPTIONS
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            final MethodArgumentNotValidException ex,
            final HttpHeaders headers,
            final HttpStatusCode status,
            final WebRequest request
    ) {
        final BindingResult result = ex.getBindingResult();
        final List<ConstraintViolationEntryDto> errors = result.getFieldErrors().stream()
                .map(fieldError -> ConstraintViolationEntryDto.builder()
                        .fieldName(fieldError.getField())
                        .wrongValue(Optional.ofNullable(fieldError.getRejectedValue()).map(Object::toString).orElse(null))
                        .errorMessage(fieldError.getDefaultMessage())
                        .build())
                .toList();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errors);
    }

    @ExceptionHandler(value = {ConstraintViolationException.class})
    public ResponseEntity<List<ConstraintViolationEntryDto>> handleConstraintViolationException(@NonNull final ConstraintViolationException exception) {
        final List<ConstraintViolationEntryDto> errorList = exception.getConstraintViolations().stream()
                .map(ConstraintViolationEntryDto::new)
                .toList();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorList);
    }


    // UNHANDLED EXCEPTIONS
    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<List<Void>> handleException(@NonNull final Exception exception) {
        log.error("GlobalExceptionHandler.handleException: ", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @ExceptionHandler(value = {ConfigurationNotReadableException.class})
    public ResponseEntity<ServerMessageDto> handleConfigurationNotReadableException(@NonNull final ConfigurationNotReadableException exception) {
        final String message = String.format("There is an issue with a configuration entry in the database! ConfigurationNotReadableException: %s", exception.getMessage());
        log.error(message, exception);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ServerMessageDto.builder()
                        .message(message)
                        .build()
                );
    }

    // 400er
    @ExceptionHandler(value = {HttpBadRequestException.class})
    public ResponseEntity<ServerMessageDto> handleHttpBadRequestException(@NonNull final HttpBadRequestException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ServerMessageDto.builder()
                        .message(exception.getMessage())
                        .build()
                );
    }

    @ExceptionHandler(HttpUnauthorizedException.class)
    public ResponseEntity<ServerMessageDto> handleHttpUnauthorizedException(@NonNull final HttpUnauthorizedException exception) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ServerMessageDto.builder()
                        .message("You are not authorized to access this resource.")
                        .build()
                );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ServerMessageDto> handleAccessDeniedException(@NonNull final AccessDeniedException exception) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ServerMessageDto.builder()
                        .message("You are not authorized to access this resource.")
                        .build()
                );
    }

    @ExceptionHandler(HttpForbiddenException.class)
    public ResponseEntity<ServerMessageDto> handleHttpForbiddenException(@NonNull final HttpForbiddenException exception) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .build();
    }

    @ExceptionHandler(value = {HttpNotFoundException.class})
    public ResponseEntity<Void> handleHttpNotFoundException(@NonNull final HttpNotFoundException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .build();
    }

    @ExceptionHandler(value = {HttpUnprocessableEntityException.class})
    public ResponseEntity<List<Void>> handleHttpUnprocessableEntityException(@NonNull final HttpUnprocessableEntityException exception) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
    }

    @ExceptionHandler(value = {HttpTimeoutException.class})
    public ResponseEntity<List<Void>> handleHttpTimeoutException(@NonNull final HttpTimeoutException exception) {
        return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).build();
    }

}