package com.rcb.dto.map.cluster;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@Schema
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClusterPointDto {

    @Schema
    @JsonProperty()
    private BigDecimal x;

    @Schema
    @JsonProperty()
    private BigDecimal y;

}
