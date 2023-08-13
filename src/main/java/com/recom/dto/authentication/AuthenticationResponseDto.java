package com.recom.dto.authentication;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Schema
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthenticationResponseDto implements Serializable {

    @Schema
    @JsonProperty()
    private String token;

    @Schema
    @JsonProperty()
    private Date issuedAt;

    @Schema
    @JsonProperty()
    private Date expiresAt;

    @Schema
    @JsonProperty()
    private BigDecimal expiresInSeconds;

    @Schema
    @JsonProperty()
    private BigDecimal expiresInMilliseconds;

}
