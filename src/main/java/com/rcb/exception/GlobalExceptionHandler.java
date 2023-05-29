package com.rcb.exception;

import com.rcb.dto.BadRequestDto;
import com.rcb.dto.ConstraintViolationEntryDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

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

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<List<Void>> handleException(@NonNull final Exception exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @ExceptionHandler(value = {HttpUnprocessableEntityException.class})
    public ResponseEntity<List<Void>> handleHttpUnprocessableEntityException(@NonNull final HttpUnprocessableEntityException exception) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
    }

    @ExceptionHandler(value = {HttpBadRequestException.class})
    public ResponseEntity<BadRequestDto> handleHttpBadRequestException(@NonNull final HttpBadRequestException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(BadRequestDto.builder()
                        .message(exception.getMessage())
                        .build()
                );
    }

}