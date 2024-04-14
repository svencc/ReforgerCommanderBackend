package com.recom.configuration;

import com.recom.dto.cache.CacheStatisticsDto;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.AccessedExpiryPolicy;
import javax.cache.expiry.Duration;

@Configuration
@RequiredArgsConstructor
public class EHCacheConfiguration {

    @NotNull
    private static final Duration DEFAULT_APPLICATION_CACHE_DURATION = javax.cache.expiry.Duration.ONE_HOUR;


    @Bean
    @NotNull
    public JCacheManagerCustomizer ehcache3TojCacheManagerBridgeCustomizer() {
        return this::addCaches;
    }

    private void addCaches(@NotNull final CacheManager eh107CacheManager) {
        // hier den 107 CacheManager konfigurieren ... das funktioniert auch; aber vermute der benutzt nicht ehcache
        final MutableConfiguration<Object, CacheStatisticsDto> configuration = applicationCacheConfiguration(Object.class, CacheStatisticsDto.class);
        final Cache<Object, CacheStatisticsDto> cache = eh107CacheManager.createCache("com.recom.service.cache.ApplicationCacheTester.testWithoutKey()", configuration);
        // final Cache<Object, CacheStatisticsDto> cache2 = eh107CacheManager.createCache("com.recom.service.cache.ApplicationCacheTester.testWithoutKey", configuration);

        // TEMPLATE FOR CREATING OTHER CACHES:
        // final Cache<Object, CacheStatisticsDto> otherCache = eh107CacheManager.createCache("other", configuration);

        // GETTING CONFIGURATION IN DIFFERENT FLAVORS:
        // final CompleteConfiguration<Object, CacheStatisticsDto> completeConfiguration = cache.getConfiguration(CompleteConfiguration.class);
        // final Eh107Configuration<Object, CacheStatisticsDto> eh107Configuration = cache.getConfiguration(Eh107Configuration.class);
        // final CacheRuntimeConfiguration<Object, CacheStatisticsDto> runtimeConfiguration = eh107Configuration.unwrap(CacheRuntimeConfiguration.class);
        // System.out.println("completeConfiguration: " + completeConfiguration);
    }

    @NotNull
    private <KEY, VALUE> MutableConfiguration<KEY, VALUE> applicationCacheConfiguration(
            @NotNull final Class<KEY> keyType,
            @NotNull final Class<VALUE> valueType
    ) {
        final MutableConfiguration<KEY, VALUE> configuration = new MutableConfiguration<>();
        configuration.setTypes(keyType, valueType);
        configuration.setExpiryPolicyFactory(AccessedExpiryPolicy.factoryOf(DEFAULT_APPLICATION_CACHE_DURATION));

        return configuration;
    }

}


