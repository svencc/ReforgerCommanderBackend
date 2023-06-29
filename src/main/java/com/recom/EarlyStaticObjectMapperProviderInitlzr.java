package com.recom;


import com.recom.service.PostStartExecutable;
import com.recom.service.provider.StaticObjectMapperProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Order(0)
@Component
@RequiredArgsConstructor
public class EarlyStaticObjectMapperProviderInitlzr implements ApplicationRunner {

    @NonNull
    private final StaticObjectMapperProvider staticObjectMapperProvider;


    @Override
    public void run(final ApplicationArguments args) throws Exception {
        log.info("* PostStartupExecutor: Application started; initialize StaticObjectMapperProvider now.");
        staticObjectMapperProvider.postConstruct();
    }

}