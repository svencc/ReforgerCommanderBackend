package com.rcb.dto.map.cluster;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.rcb.dto.map.Point2DBaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.experimental.SuperBuilder;

@Schema
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClusterPointDto extends Point2DBaseDto {

}
