package com.recom.dto.situationpicture.mapobjects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.recom.dto.map.Point2DDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@Schema
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MapRenderCommandDto implements Serializable {

    @Schema
    @JsonProperty()
    private UUID id;

    @Schema
    @JsonProperty()
    private String clusterList;

    @Schema
    @JsonProperty()
    private List<Point2DDto> geometry;

    @Schema
    @JsonProperty()
    private Point2DDto coordinates;

    @Schema
    @JsonProperty()
    private String text;

    @Schema
    @JsonProperty()
    private Integer color;

    @Schema
    @JsonProperty()
    private Integer colorOutline;

    @Schema
    @JsonProperty()
    private String zIndex;

    @Schema
    @JsonProperty()
    private MapObjectType belongsToGroup;

    @Schema
    @JsonProperty()
    @Builder.Default
    private List<String> tags = new ArrayList<>();

}
