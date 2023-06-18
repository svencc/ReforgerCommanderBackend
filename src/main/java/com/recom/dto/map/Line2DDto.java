package com.recom.dto.map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@Schema
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Line2DDto implements Serializable {

    @Schema
    @JsonProperty()
    private Point2DDto start;

    @Schema
    @JsonProperty()
    private Point2DDto end;

}
