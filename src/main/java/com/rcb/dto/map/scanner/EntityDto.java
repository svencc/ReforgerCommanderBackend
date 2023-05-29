package com.rcb.dto.map.scanner;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EntityDto {

    @Schema
    @JsonProperty()
    private String entityId;

    @Schema
    @JsonProperty()
    private String className;

    @Schema
    @JsonProperty()
    private String resourceName;

    @Schema
    @JsonProperty()
    private List<BigDecimal> rotationX;

    @Schema
    @JsonProperty()
    private List<BigDecimal> rotationY;

    @Schema
    @JsonProperty()
    private List<BigDecimal> rotationZ;

    @Schema
    @JsonProperty()
    private List<BigDecimal> coords;

}
