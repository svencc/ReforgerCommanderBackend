package com.rcb.dto.map.scanner;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Schema
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EntityPackageDto {

    @Schema
    @JsonProperty()
    private String sessionIdentifier; // <<< der fehlt noch auf client seite

    @Schema
    @JsonProperty()
    private Integer packageOrder;

    @Schema
    @JsonProperty()
    private List<EntityDto> entities;

}
