package com.recom.dto.cache;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Schema
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CacheStatisticsDto {

    @Schema()
    private List<CacheInfoDto> caches;

}
