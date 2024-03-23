package com.recom.dto.map.scanner;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MapTopographyChunkScanRequestDto {

    @NotEmpty
    @Schema
    @JsonProperty()
    private String mapName;

    @NotEmpty
    @Schema
    @JsonProperty()
    private Integer chunkCoordinateX;

    @NotEmpty
    @Schema
    @JsonProperty()
    private Integer chunkCoordinateY;

}
