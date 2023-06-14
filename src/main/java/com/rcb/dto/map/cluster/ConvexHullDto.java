package com.rcb.dto.map.cluster;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rcb.dto.map.Point2DDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;

@Data
@Schema
@SuperBuilder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConvexHullDto implements Serializable {

//    @Schema
//    @JsonProperty()
//    private List<Line2DDto> lines;

    @Schema
    @JsonProperty()
    private List<Point2DDto> vertices;

}
