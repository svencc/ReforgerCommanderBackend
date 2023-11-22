package com.recom.persistence.dbcached;

import com.recom.entity.DBCachedItem;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
interface DatabasePersistentCacheRepository extends JpaRepository<DBCachedItem, String> {

    @NonNull
    Optional<DBCachedItem> findByCacheNameAndCacheKey(
            @NonNull final String cacheName,
            @NonNull final String key
    );

}