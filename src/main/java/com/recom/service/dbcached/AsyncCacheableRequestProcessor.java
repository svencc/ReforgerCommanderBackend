package com.recom.service.dbcached;

import com.recom.exception.DBCachedDeserializationException;
import com.recom.service.MutexService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.io.Serializable;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncCacheableRequestProcessor {

    @NonNull
    private final MutexService mutexService;
    @NonNull
    private final DBCachedManager dbCachedManager;
    //    @NonNull
//    private final CacheManager cacheManager; // <<<<<<<<<<<<<< dann wäre hier der cache manager zu injecten, dann pushen wir die werte automatisch in den applikationscache, und können die werte dann auch über den cache manager abfragen!
    @NonNull
    private final ExecutorProvider executorProvider;

// @TODO: sollen die Methoden überhaupt noch mit @Cacheable versehen werden?

    @NonNull
    public <T extends Serializable> ResponseEntity<T> processRequest(
            @NonNull final String cacheName,
            @NonNull final String cacheKey, // @TODO CacheKey Object
            @NonNull final Supplier<T> cacheLoader // @TODO CacheKey is Passed to CacheLoader (no supplier, another signature: CacheLoader -> Optional : cacheLoader(CacheKey))
    ) {
        if (dbCachedManager.isCached(cacheName, cacheKey)) {
            try {
                final Optional<T> maybeCachedValue = dbCachedManager.get(cacheName, cacheKey);
                return maybeCachedValue.map(value -> ResponseEntity.status(HttpStatus.OK)
                                .cacheControl(CacheControl.noCache())
                                .body(value)
                        )
                        .orElseGet(() -> ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .cacheControl(CacheControl.noCache())
                                .build()
                        );
            } catch (DBCachedDeserializationException dbcde) {
                log.error("Unable to deserialize cached value; Generating new one");
                dbCachedManager.delete(cacheName, cacheKey);
            }
        }

        final String mutexFormat = String.format("%s#%s", cacheName, cacheKey);

        boolean claimed = mutexService.claim(mutexFormat);
        if (claimed) {
            log.debug("Generating data for cache key {}.", cacheKey);

            CompletableFuture.supplyAsync(() -> {
                final StopWatch stopwatch = new StopWatch();
                stopwatch.start();

                Optional<T> result = Optional.empty();
                try {
                    result = Optional.ofNullable(cacheLoader.get());
                } catch (Exception e) {
                    log.error("Async-Exception", e);
                } finally {
                    result.ifPresent(value -> dbCachedManager.put(cacheName, cacheKey, value));
                    mutexService.release(mutexFormat);
                    stopwatch.stop();
                    log.debug("Generated {} in {} ms.", mutexFormat, stopwatch.getTotalTimeMillis());
                }

                return result;
            }, executorProvider.provideClusterGeneratorExecutor());

            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .cacheControl(CacheControl.noCache())
                    .build();
        }

        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .cacheControl(CacheControl.noCache())
                .build();
    }

}

