package com.recom.dto.map.mapcomposer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@Data
@Schema
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MapLayerRasterizerConfigurationDto {

    @Nullable
    @Schema
    @JsonProperty()
    public String rasterizerName;

    @Nullable
    @Schema
    @JsonProperty()
    public Boolean enabled;

    @Nullable
    @Schema
    @JsonProperty()
    public Boolean visible;

    @Nullable
    @Schema
    @JsonProperty()
    public Boolean sequentialCoreData;

    @Nullable
    @Schema
    @JsonProperty()
    public Integer layerOrder;

}
