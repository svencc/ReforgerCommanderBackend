package com.recom.tacview.service;

import jakarta.annotation.PostConstruct;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public final class RandomProvider {

    @Nullable
    private Random[] instancePool;
    private int poolSize = 10;
    private int currentInstance = 0;

    @PostConstruct
    public void postConstruct() {
        instancePool = new Random[poolSize];
        for (int i = 0; i < poolSize; i++) {
            instancePool[i] = new Random();
        }
    }

    public Random provide() {
        currentInstance = (currentInstance + 1) % 10;

        return instancePool[currentInstance];
    }

}