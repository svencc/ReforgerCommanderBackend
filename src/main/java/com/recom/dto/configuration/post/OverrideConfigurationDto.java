package com.recom.dto.configuration.post;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.recom.model.configuration.ConfigurationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
@Schema
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OverrideConfigurationDto implements Serializable {

    @NotBlank
    @Schema
    @JsonProperty()
    private String namespace;

    @NotBlank
    @Schema
    @JsonProperty()
    private String name;

    @NotNull
    @Schema
    @JsonProperty()
    private ConfigurationType type;

    @NotBlank
    @Schema
    @JsonProperty()
    private String mapOverrideValue;

}
