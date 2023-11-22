package com.recom.dto.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.recom.model.configuration.ConfigurationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Schema
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OverrideConfigurationDto implements Serializable, OverridableConfigurationInterface {

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
    private String mapOverriddenValue;

    @Schema
    @JsonProperty()
    private List<String> mapOverriddenListValue;

//    @AssertTrue(com.recom.dto.message = "Only one of [mapOverrideValue or mapOverrideListValue] can be and must be set!")
//    public boolean isValueOrListValueSetAssertion() {
//        final boolean onlyListValueIsSet = mapOverriddenListValue != null && mapOverrideValue == null;
//        final boolean onlyValueIsSet = mapOverrideValue != null && mapOverriddenListValue == null;
//
//        if (type.equals(ConfigurationType.LIST)) {
//            return onlyListValueIsSet;
//        } else {
//            return onlyValueIsSet && !mapOverrideValue.isBlank();
//        }
//    }

}
