package com.recom.dto.map.renderer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
@Schema
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MapRendererRequestDto implements Serializable {

    @NotBlank
    @Schema
    @JsonProperty()
    private String mapName;

}
