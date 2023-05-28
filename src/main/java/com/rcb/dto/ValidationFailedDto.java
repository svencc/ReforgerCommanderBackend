package com.rcb.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ValidationFailedDto {

    @JsonProperty
    @Schema(description = "Object name where validation failed", example = "name")
    private String validatedObject;


    @JsonProperty
    @Schema(description = "Error description", example = "Validation failed for argument [0] in public org.springframework.http.ResponseEntity<cc.sven.springboottemplate.dto.CustomMessageDto> cc.sven.springboottemplate.api.CustomMessageController.createCustomMessage(cc.sven.springboottemplate.dto.CustomMessageDto): [Field error in object 'customMessageDto' on field 'recipient': rejected value [Emil]; codes [Email.customMessageDto.recipient,Email.recipient,Email.java.lang.String,Email]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [customMessageDto.recipient,recipient]; arguments []; default message [recipient],[Ljavax.validation.constraints.Pattern$Flag;@dbad3c5,.*]; default message [muss eine korrekt formatierte E-Mail-Adresse sein]]")
    private String message;

}