package com.recom.tacview.service;

import com.recom.tacview.property.RendererProperties;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Service
@RequiredArgsConstructor
public class RendererExecutorProvider {

    @NonNull
    private final RendererProperties rendererProperties;


    @NonNull
    public ExecutorService provideNewExecutor() {
        // return Executors.newFixedThreadPool(rendererProperties.getThreadPoolSize());
        // return Executors.newVirtualThreadPerTaskExecutor();
        return Executors.newVirtualThreadPerTaskExecutor();
    }


}
