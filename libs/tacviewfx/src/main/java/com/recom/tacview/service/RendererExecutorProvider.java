package com.recom.tacview.service;

import com.recom.tacview.property.RendererProperties;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Service
@RequiredArgsConstructor
public class RendererExecutorProvider {

    @NonNull
    private final RendererProperties rendererProperties;
    @Nullable
    private ExecutorService fixedThreadPoolSingletonInstance;


    @NonNull
    public ExecutorService provideNewExecutor() {
        if (rendererProperties.getThreadPoolSize() <= 0) {
            return Executors.newVirtualThreadPerTaskExecutor();
        } else {
            if (fixedThreadPoolSingletonInstance == null) {
                fixedThreadPoolSingletonInstance = Executors.newFixedThreadPool(rendererProperties.getThreadPoolSize());
            }

            return fixedThreadPoolSingletonInstance;
        }
    }

}
