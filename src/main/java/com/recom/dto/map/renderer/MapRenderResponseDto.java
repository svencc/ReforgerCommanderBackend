package com.recom.dto.map.renderer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@Schema
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MapRenderResponseDto implements Serializable {

    @Schema
    @JsonProperty()
    private List<MapRenderCommandDto> renderCommands;

//    @Schema
//    @JsonProperty(value = "zIndexMin")
//    private BigDecimal zIndexMin;
//
//    @Schema
//    @JsonProperty(value = "zIndexMax")
//    private BigDecimal zIndexMax;

}
