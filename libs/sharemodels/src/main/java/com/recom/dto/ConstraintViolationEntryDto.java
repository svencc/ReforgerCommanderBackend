package com.recom.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import lombok.*;

import java.util.Iterator;

@Data
@Schema
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConstraintViolationEntryDto {

    @Schema
    private String fieldName;
    @Schema
    private String wrongValue;
    @Schema
    private String errorMessage;

    public ConstraintViolationEntryDto(@NonNull final ConstraintViolation violation) {
        final Iterator<Path.Node> iterator = violation.getPropertyPath().iterator();
        final Path.Node currentNode = iterator.next();
        String invalidValue = "";
        if (violation.getInvalidValue() != null) {
            invalidValue = violation.getInvalidValue().toString();
        }
        this.fieldName = currentNode.getName();
        this.wrongValue = invalidValue;
        this.errorMessage = violation.getMessage();
    }

}
