package com.recom.service;

import com.recom.config.AsyncConfiguration;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ExecutorProvider {

    @NonNull
    private final ThreadPoolTaskExecutor clusterGeneratorExecutor;

    @NonNull
    public ExecutorProvider(
            @Qualifier(AsyncConfiguration.CLUSTER_GENERATOR_EXECUTOR_BEAN) @NonNull final ThreadPoolTaskExecutor executor
    ) {
        this.clusterGeneratorExecutor = executor;
    }

    @NonNull
    public ThreadPoolTaskExecutor provideClusterGeneratorExecutor() {
        return this.clusterGeneratorExecutor;
    }

}
