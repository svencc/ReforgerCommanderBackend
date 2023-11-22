package com.recom.service;

import lombok.NonNull;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
public class MutexService {

    @NonNull
    private final Object lock = new Object();
    @NonNull
    private final Set<String> semaphore = new HashSet<>();

    @Synchronized("lock")
    public boolean claim(@NonNull final String resource) {
        if (semaphore.contains(resource)) {
            log.warn(String.format("Cannot claim resource '%s'", resource));
            return false;
        } else {
            log.debug(String.format("Claim resource '%s'", resource));
            semaphore.add(resource);
            return true;
        }
    }

    @Synchronized("lock")
    public void release(@NonNull final String resource) {
        if (semaphore.contains(resource)) {
            log.debug(String.format("Release resource '%s'", resource));
            semaphore.remove(resource);
        }
    }

}
