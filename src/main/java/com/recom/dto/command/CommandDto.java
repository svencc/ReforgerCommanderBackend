package com.recom.dto.command;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.recom.model.command.CommandType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

@Data
@Schema
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommandDto {

    @Schema
    @JsonProperty()
    private Long id;

    @Schema
    @JsonProperty()
    private String mapName;

    @Schema
    @JsonProperty()
    private CommandType commandType;

    @Schema(description = "Unix timestamp in milliseconds", example = "1691941419964")
    @JsonProperty()
    private Long timestampEpochMilliseconds;

    @Schema
    @JsonProperty()
    private String payload;

}