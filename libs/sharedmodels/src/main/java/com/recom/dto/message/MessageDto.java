package com.recom.dto.message;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Schema
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageDto {

    @Schema
    @JsonProperty()
    private UUID uuid;

    @Schema
    @JsonProperty()
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private MessageType messageType;

    @Schema(description = "Unix timestamp in milliseconds", example = "1691941419964")
    @JsonProperty()
    private String timestampEpochMilliseconds;

    @Schema
    @JsonProperty()
    private String payload;

}