package com.recom.dto.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.recom.model.configuration.ConfigurationType;
import com.recom.service.provider.StaticObjectMapperProvider;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.List;

@Data
@Schema
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OverridableConfigurationDto implements Serializable, OverridableConfigurationInterface {

    @Schema
    @JsonProperty()
    private String namespace;

    @Schema
    @JsonProperty()
    private String name;

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
            return StaticObjectMapperProvider.provide().readValue(defaultValue, new TypeReference<List<String>>() {
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
        if (type == ConfigurationType.LIST && mapOverriddenValue != null) {
            return StaticObjectMapperProvider.provide().readValue(mapOverriddenValue, new TypeReference<List<String>>() {
            });
        } else {
            return null;
        }
    }

}
