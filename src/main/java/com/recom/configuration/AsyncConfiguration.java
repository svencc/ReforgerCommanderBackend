package com.recom.configuration;

import com.recom.exception.AsyncExceptionHandler;
import com.recom.property.RECOMAsyncProperties;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Slf4j
@EnableAsync
@Configuration
@RequiredArgsConstructor
public class AsyncConfiguration implements AsyncConfigurer {

    public static final String ASYNC_REQUEST_PROCESSOR_EXECUTOR_BEAN = "AsyncRequestProcessor";

    @NonNull
    private final RECOMAsyncProperties recomAsyncProperties;

    // First Async Executor
    @Primary
    @Override
    public SimpleAsyncTaskExecutor getAsyncExecutor() {
        final SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor("Async-Executor");

        return executor;
    }

    @Override
    public AsyncExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new AsyncExceptionHandler();
    }

    // Additional Async Executor(s)
    @Bean("AsyncMapTransactionExecutor")
    @Qualifier(value = "AsyncMapTransactionExecutor")
    public ThreadPoolTaskExecutor getprovide() {
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setThreadNamePrefix("MapEvnt-Exec");
        executor.initialize();

        return executor;
    }

    @Bean("ConfigurationSystemExecutor")
    @Qualifier(value = "ConfigurationSystemExecutor")
    public ThreadPoolTaskExecutor provideAsyncConfigurationSystemExecutor() {
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setThreadNamePrefix("CnfEvnt-Exec");
        executor.initialize();

        return executor;
    }

    @Bean("CacheResetExecutor")
    @Qualifier(value = "CacheResetExecutor")
    public ThreadPoolTaskExecutor provideCacheResetExecutorExecutor() {
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setThreadNamePrefix("CnfEvnt-Exec");
        executor.initialize();

        return executor;
    }

    @Bean(ASYNC_REQUEST_PROCESSOR_EXECUTOR_BEAN)
    @Qualifier(value = ASYNC_REQUEST_PROCESSOR_EXECUTOR_BEAN)
    public ThreadPoolTaskExecutor provideClusterGeneratorExecutor() {
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(recomAsyncProperties.getCorePoolSize());
        executor.setMaxPoolSize(recomAsyncProperties.getMaxPoolSize());
        executor.setThreadNamePrefix("Request-Processor");
        executor.initialize();

        return executor;
    }

}