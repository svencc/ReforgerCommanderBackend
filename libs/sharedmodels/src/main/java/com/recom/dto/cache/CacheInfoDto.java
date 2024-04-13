package com.recom.dto.cache;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Schema
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CacheInfoDto {

    @Schema()
    private String name;
    @Schema()
    private Long size;
    @Schema
    private Set<Object> keys;
    @Schema
    private String stats;

}
