package com.recom.dto.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.recom.model.configuration.ConfigurationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
@Schema
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConfigurationDto implements Serializable {

    @Schema
    @JsonProperty()
    private String mapName;

    @Schema
    @JsonProperty()
    private String namespace;

    @Schema
    @JsonProperty()
    private String name;

    @Schema
    @JsonProperty()
    private ConfigurationType type;

}
