package com.recom.dto.configuration.post;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.recom.model.configuration.ConfigurationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
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

    @Schema
    @JsonProperty()
    private String mapOverrideValue;

    @Schema
    @JsonProperty()
    private List<String> mapOverrideListValue;

    @AssertTrue(message = "Only one of [mapOverrideValue or mapOverrideListValue] can be and must be set!")
    public boolean isValueOrListValueSetAssertion() {
        final boolean onlyListValueIsSet = mapOverrideListValue != null && mapOverrideValue == null;
        final boolean onlyValueIsSet = mapOverrideValue != null && mapOverrideListValue == null;

        if (type.equals(ConfigurationType.LIST)) {
            return onlyListValueIsSet;
        } else {
            return onlyValueIsSet && !mapOverrideValue.isBlank();
        }
    }

}
