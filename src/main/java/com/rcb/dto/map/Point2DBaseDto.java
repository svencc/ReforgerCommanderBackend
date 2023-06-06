package com.rcb.dto.map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Schema
@SuperBuilder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
abstract public class Point2DBaseDto implements Serializable {

    @Schema
    @JsonProperty()
    private BigDecimal x;

    @Schema
    @JsonProperty()
    private BigDecimal y;

}
