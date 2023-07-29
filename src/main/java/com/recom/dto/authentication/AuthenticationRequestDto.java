package com.recom.dto.authentication;

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
public class AuthenticationRequestDto implements Serializable {

    @Schema
    @JsonProperty()
    private String username;

    @Schema
    @JsonProperty()
    private String password;

}
