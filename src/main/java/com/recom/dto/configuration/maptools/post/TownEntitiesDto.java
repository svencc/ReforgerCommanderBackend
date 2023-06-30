package com.recom.dto.configuration.maptools.post;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@Schema
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TownEntitiesDto implements Serializable {

    @NotBlank
    @Schema
    @JsonProperty()
    private String mapName;

    @NotNull
    @Schema
    @JsonProperty()
    private List<String> townEntitiesMatcherToSet;

}
