package com.recom.service.cache;

import com.recom.dto.cache.CacheInfoDto;
import com.recom.dto.cache.CacheStatisticsDto;
import jakarta.annotation.Nullable;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.cache.annotation.CacheKey;
import javax.cache.annotation.CacheResult;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationCacheTester {

    @NonNull
    @CacheResult(cacheName = "com.recom.service.cache.ApplicationCacheTester.testWithoutKey")
    public CacheStatisticsDto testWithoutKey() {
        return CacheStatisticsDto.builder()
                .caches(List.of(
                        CacheInfoDto.builder()
                                .name("test-without-key")
                                .size(1L)
                                .stats("test")
                                .build()
                ))
                .build();
    }

    @NonNull
    @CacheResult(cacheName = "com.recom.service.cache.ApplicationCacheTester.testWithOneKey")
    public CacheStatisticsDto testWithOneKey(@NonNull final String key) {
        return CacheStatisticsDto.builder()
                .caches(List.of(
                        CacheInfoDto.builder()
                                .name("test-with-one-key: " + key)
                                .size(1L)
                                .stats("test")
                                .build()
                ))
                .build();
    }

    @NonNull
    @CacheResult(cacheName = "com.recom.service.cache.ApplicationCacheTester.testWithMultipleKeys")
    public CacheStatisticsDto testWithMultipleKeys(
            @NonNull final String key1,
            @NonNull final String key2
    ) {
        return CacheStatisticsDto.builder()
                .caches(List.of(
                        CacheInfoDto.builder()
                                .name("test-with-multiple-keys: " + key1 + ", " + key2)
                                .size(1L)
                                .stats("test")
                                .build()
                ))
                .build();
    }

}