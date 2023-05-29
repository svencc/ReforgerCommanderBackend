package com.rcb.dto.map.scanner;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Schema
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionalEntityPackageDto {

    // DTO validation missing completely; from Controller, via DTO, to ValidationErrorDto messagen @TODO
    @NotEmpty
    @Schema
    @JsonProperty()
    private String sessionIdentifier;

    @Schema
    @JsonProperty()
    private Integer packageOrder;

    @Schema
    @JsonProperty()
    @Builder.Default
    private List<EntityDto> entities = new ArrayList<>();

}