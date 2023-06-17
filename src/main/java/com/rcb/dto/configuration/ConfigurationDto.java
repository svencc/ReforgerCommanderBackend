package com.rcb.dto.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rcb.dto.map.Point2DDto;
import com.rcb.dto.map.cluster.ConcaveHullDto;
import com.rcb.dto.map.cluster.ConvexHullDto;
import com.rcb.model.configuration.ConfigurationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.Nationalized;

import java.io.Serializable;
import java.util.List;

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
