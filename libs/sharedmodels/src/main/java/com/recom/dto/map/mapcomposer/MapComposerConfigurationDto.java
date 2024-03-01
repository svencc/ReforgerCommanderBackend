package com.recom.dto.map.mapcomposer;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;

@Data
@Schema
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MapComposerConfigurationDto {

    @Nullable
    @Schema
    @JsonProperty()
    private MapDesignSchemeDto mapDesignScheme;

    @Nullable
    @Schema
    @JsonProperty()
    @Builder.Default
    private List<MapLayerRasterizerConfigurationDto> rendererConfiguration = new ArrayList<>();

}
