package com.rcb.dto.map.meta;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
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
public class MapMetaDto implements Serializable {

    @Schema
    @JsonProperty()
    private String name;

    @Schema
    @JsonProperty()
    private Integer entitiesCount;

    @Schema
    @JsonProperty()
    private List<String> utilizedClasses;

    @Schema
    @JsonProperty()
    private List<String> utilizedResources;

    @Schema
    @JsonProperty()
    private List<String> utilizedPrefabs;

    @Schema
    @JsonProperty()
    private List<String> namedEntities;

}
