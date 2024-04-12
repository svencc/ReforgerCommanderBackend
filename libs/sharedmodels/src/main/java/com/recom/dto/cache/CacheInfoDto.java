package com.recom.dto.cache;

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
public class CacheInfoDto {

    @Schema()
    private String name;
    @Schema()
    private Integer size;
    @Schema
    private Set<Object> keys;
    @Schema
    private String stats;

}
