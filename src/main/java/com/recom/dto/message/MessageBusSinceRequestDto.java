package com.recom.dto.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Schema
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageBusSinceRequestDto implements Serializable {

    @NotBlank
    @Schema
    @JsonProperty()
    private String mapName;

    @NotNull
    @Schema(description = "Unix timestamp in milliseconds", example = "1691941419964")
    @JsonProperty()
    private Long timestampEpochMilliseconds;

}
