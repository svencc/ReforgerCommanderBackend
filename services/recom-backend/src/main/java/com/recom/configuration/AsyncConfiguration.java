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
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executors;

@Slf4j
@EnableAsync
@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
public class AsyncConfiguration implements AsyncConfigurer {

    public static final String ASYNC_REQUEST_PROCESSOR_EXECUTOR_BEAN = "AsyncRequestProcessor";
    public static final String VIRTUAL_THREAD_PER_TASK_EXECUTOR_BEAN = "VirtualThreadPerTaskExecutor";

    @NonNull
    private final RECOMAsyncProperties recomAsyncProperties;

    // First Async Executor
    @Primary
    @Override
    public AsyncTaskExecutor getAsyncExecutor() {
        return new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor()); // TODO <<<<<<<<<<<<<<<< migrate all to this; especially the async map transaction executor
//        final SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor("Async-Executor");
//
//        return executor;
    }

    @Override
    public AsyncExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new AsyncExceptionHandler();
    }

    // Additional Async Executor(s)
    @Bean("AsyncMapTransactionExecutor")
    @Qualifier(value = "AsyncMapTransactionExecutor")
    public ThreadPoolTaskExecutor getAsyncMapTransactionExecutor() {
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setThreadNamePrefix("MapEvnt-Exec");
        executor.initialize();

        return executor;
    }

    @Bean("AsyncMapTopographyTransactionExecutor")
    @Qualifier(value = "AsyncMapTopographyTransactionExecutor")
    public ThreadPoolTaskExecutor getAsyncMapTopographyTransactionExecutor() {
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setThreadNamePrefix("MpTpEvnt-Xc");
        executor.initialize();

        return executor;
    }

    @Bean("ConfigurationSystemExecutor")
    @Qualifier(value = "ConfigurationSystemExecutor")
    public ThreadPoolTaskExecutor provideConfigurationSystemExecutor() {
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setThreadNamePrefix("CnfEvnt-Exec");
        executor.initialize();

        return executor;
    }

    @Bean("CacheResetExecutor")
    @Qualifier(value = "CacheResetExecutor")
    public ThreadPoolTaskExecutor provideCacheResetExecutor() {
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

    @Bean(VIRTUAL_THREAD_PER_TASK_EXECUTOR_BEAN)
    @Qualifier(value = VIRTUAL_THREAD_PER_TASK_EXECUTOR_BEAN)
    public AsyncTaskExecutor provideVirtualThreadPerTaskExecutor() {
        return new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor());
    }

}