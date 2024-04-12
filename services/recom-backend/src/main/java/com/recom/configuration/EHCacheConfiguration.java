package com.recom.configuration;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.jsr107.Eh107Configuration;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class EHCacheConfiguration {

    @NotNull
    private static final Duration DEFAULT_APPLICATION_CACHE_DURATION = Duration.ofMinutes(999);


//    @Bean
//    @NotNull
//    public JCacheManagerCustomizer jCacheManagerCustomizer() {
//        return (jcacheManager) -> {
//            final CacheConfiguration<UUID, Optional> ehCacheConfig = CacheConfigurationBuilder.newCacheConfigurationBuilder(
//                            UUID.class,
//                            Optional.class,
//                            ResourcePoolsBuilder
//                                    .heap(100)
//                                    .offheap(10, MemoryUnit.MB)
//                    )
//                    .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(DEFAULT_APPLICATION_CACHE_DURATION))
//                    .build();
//
//            jcacheManager.createCache("UserPersistenceLayer.findByUUID", Eh107Configuration.fromEhcacheCacheConfiguration(ehCacheConfig));
//        };
//    }
//
//    @Bean
//    @NotNull
//    public JCacheManagerCustomizer jCacheManagerCustomizer2() {
//        return (jcacheManager) -> {
//            final CacheConfiguration<UUID, Optional> ehCacheConfig = CacheConfigurationBuilder.newCacheConfigurationBuilder(
//                            UUID.class,
//                            Optional.class,
//                            ResourcePoolsBuilder
//                                    .heap(100)
//                                    .offheap(10, MemoryUnit.MB)
//                    )
//                    .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(DEFAULT_APPLICATION_CACHE_DURATION))
//                    .build();
//
//            jcacheManager.createCache("UserPersistenceLayer.findByUUID", Eh107Configuration.fromEhcacheCacheConfiguration(ehCacheConfig));
//        };
//    }


}


