package com.recom.service.cache;

import com.recom.dto.cache.CacheInfoDto;
import com.recom.dto.cache.CacheStatisticsDto;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.stream.Streams;
import org.hibernate.SessionFactory;
import org.hibernate.stat.CacheRegionStatistics;
import org.hibernate.stat.Statistics;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheStatisticsProvider {

    @NonNull
    private final javax.cache.CacheManager jCacheManager;
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
                .collect(Collectors.toList());

        caches.addAll(
                Streams.of(jCacheManager.getCacheNames())
                        .map(cacheName -> Optional.ofNullable(jCacheManager.getCache(cacheName)))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .map(cache -> {
                                    final Set<Object> keys = Streams.of(cache.iterator()).map(entry -> entry.getKey().toString()).collect(Collectors.toSet());
                                    return CacheInfoDto.builder()
                                            .name(cache.getName())
                                            .keys(keys)
                                            .size((long) keys.size())
                                            .build();
                                }
                        )
                        .toList()
        );


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