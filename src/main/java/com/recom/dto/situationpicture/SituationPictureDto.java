package com.recom.dto.situationpicture;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.recom.dto.situationpicture.mapobjects.MapObjectsDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
@Schema
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SituationPictureDto implements Serializable {

    @Schema
    @JsonProperty()
    private MapObjectsDto mapObjects;

}
