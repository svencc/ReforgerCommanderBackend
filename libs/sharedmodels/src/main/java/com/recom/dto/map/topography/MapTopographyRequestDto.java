package com.recom.dto.map.topography;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.recom.dto.map.mapcomposer.MapComposerConfigurationDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.io.Serializable;

@Data
@Schema
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MapTopographyRequestDto implements Serializable {

    @NotBlank
    @Schema
    @JsonProperty()
    private String mapName;

    @Nullable
    @Schema
    @JsonProperty()
    private MapComposerConfigurationDto mapComposerConfiguration;

}