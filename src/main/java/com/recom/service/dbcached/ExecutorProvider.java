package com.recom.service.dbcached;

import com.recom.configuration.AsyncConfiguration;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

@Slf4j
@Service
class ExecutorProvider {

    @NonNull
    private final ThreadPoolTaskExecutor clusterGeneratorExecutor;

    @NonNull
    public ExecutorProvider(
            @Qualifier(AsyncConfiguration.ASYNC_REQUEST_PROCESSOR_EXECUTOR_BEAN) @NonNull final ThreadPoolTaskExecutor executor
    ) {
        this.clusterGeneratorExecutor = executor;
    }

    @NonNull
    public ThreadPoolTaskExecutor provideClusterGeneratorExecutor() {
        return this.clusterGeneratorExecutor;
    }

}
