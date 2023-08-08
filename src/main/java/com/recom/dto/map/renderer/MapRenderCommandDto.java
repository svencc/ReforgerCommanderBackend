package com.recom.dto.map.renderer;

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

    // MapRenderCommands - wir brauchen diverse Typen wie Polygon, Point, Line, Text, Image, Circle, etc.


    @Schema
    @JsonProperty()
    private UUID id; // UUID Generator

    @Schema
    @JsonProperty()
    private List<Point2DDto> geometry;  // cluster polygon

    @Schema
    @JsonProperty()
    private Point2DDto coordinates; // village name coordinates

    @Schema
    @JsonProperty()
    private String text;    // Try to get Village Name; try to render text

    @Schema
    @JsonProperty()
    private Integer color;  // all clusters have color = xxx (from database config)

    @Schema
    @JsonProperty()
    private Integer colorOutline;

    @Schema
    @JsonProperty()
    private String zIndex; // all clusters have zIndex = 0

    @Schema
    @JsonProperty()
    @Builder.Default
    private List<String> tags = new ArrayList<>();  // village / structure or so

    // density = area/points ; size (points of cluster)? area m2; <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< (META)


}
