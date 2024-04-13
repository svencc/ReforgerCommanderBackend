package com.recom.service.cache;

import com.recom.dto.cache.CacheInfoDto;
import com.recom.dto.cache.CacheStatisticsDto;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.stat.CacheRegionStatistics;
import org.hibernate.stat.Statistics;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheStatisticsProvider {

    @NonNull
    private final SessionFactory sessionFactory;


    @NonNull
    public CacheStatisticsDto provide() {
        final Statistics statistics = sessionFactory.getStatistics();
        final List<CacheInfoDto> caches = Arrays.stream(statistics.getSecondLevelCacheRegionNames())
                .map(region -> {
                    try {
                        return statistics.getDomainDataRegionStatistics(region);
                    } catch (final Exception e) {
                        log.warn("Error getting cache statistics for region: {}", region);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .map(this::getCacheInfo)
                .toList();

        return CacheStatisticsDto.builder()
                .caches(caches)
                .build();
    }

    @NonNull
    private CacheInfoDto getCacheInfo(@NonNull final CacheRegionStatistics regionStatistics) {
        return CacheInfoDto.builder()
                .name(regionStatistics.getRegionName())
                .size(regionStatistics.getSizeInMemory())
                .stats(regionStatistics.toString())
                .build();
    }

}