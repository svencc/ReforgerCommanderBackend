package com.recom.dto.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.recom.model.configuration.ConfigurationType;
import com.recom.service.provider.StaticObjectMapperProvider;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@Schema
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OverridableConfigurationDto implements Serializable {

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
    private String defaultValue;

    @Schema
    @JsonProperty()
    private List<String> defaultListValue;

    @Schema
    @JsonProperty()
    private String mapOverriddenValue;


    @Schema
    @JsonProperty()
    private List<String> mapOverriddenListValue;

    @Nullable
    public String getDefaultValue() {
        if (type == ConfigurationType.LIST) {
            return null;
        } else {
            return defaultValue;
        }
    }

    @Nullable
    @SneakyThrows
    public List<String> getDefaultListValue() {
        if (type == ConfigurationType.LIST && defaultValue != null) {
            return StaticObjectMapperProvider.provide().readValue(defaultValue, new TypeReference<>() {
            });
        } else {
            return null;
        }
    }

    @Nullable
    public String getMapOverriddenValue() {
        if (type == ConfigurationType.LIST) {
            return null;
        } else {
            return mapOverriddenValue;
        }
    }

    @Nullable
    @SneakyThrows
    public List<String> getMapOverriddenListValue() {
        if (mapOverriddenListValue != null) {
            return mapOverriddenListValue; // this is relevant for using dto as post write model!
        } else if (type == ConfigurationType.LIST && mapOverriddenValue != null) {
            return StaticObjectMapperProvider.provide().readValue(mapOverriddenValue, new TypeReference<>() {});
        } else {
            return null;
        }
    }

//    @AssertTrue(message = "Only one of [mapOverriddenValue or mapOverriddenListValue] can be and must be set!")
//    public boolean isValueOrListValueSetAssertion() {
//        final boolean onlyListValueIsSet = mapOverriddenListValue != null && mapOverriddenValue == null;
//        final boolean onlyValueIsSet = mapOverriddenValue != null && mapOverriddenListValue == null;
//
//        if (type.equals(ConfigurationType.LIST)) {
//            return onlyListValueIsSet;
//        } else {
//            return onlyValueIsSet && !mapOverriddenValue.isBlank();
//        }
//    }

}
