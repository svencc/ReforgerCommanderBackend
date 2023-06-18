package com.recom.dto.configuration.post;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.recom.dto.configuration.get.OverridableConfigurationDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
public class OverrideConfigurationListDto implements Serializable {

    @NotBlank
    @Schema
    @JsonProperty()
    private String mapName;

    @Valid
    @Schema
    @JsonProperty()
    private List<OverrideConfigurationDto> configurationList;

}
