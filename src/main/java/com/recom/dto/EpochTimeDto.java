package com.recom.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
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
public class EpochTimeDto {

    @Schema(description = "Unix timestamp in seconds", example = "1691941419")
    private long epochSeconds;

    @Schema(description = "Unix timestamp in milliseconds", example = "1691941419964")
    private long epochMilliseconds;

}
