package com.recom;


import com.recom.service.PostStartExecutable;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostStartupExecutor implements ApplicationRunner {

    @NonNull
    private final List<PostStartExecutable> registeredPostStarters = new ArrayList<>();

    public void registerPostStartRunner(@NonNull final PostStartExecutable runner) {
        log.info("Register '{}' for post startup execution.", runner.identifyPostStartRunner());
        registeredPostStarters.add(runner);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("* PostStartupExecutor: Application started; execute registered PostStartExecutables now:");

        if (registeredPostStarters.isEmpty()) {
            log.info("+--- No PostStartExecutables are registered.");
        } else {
            for (PostStartExecutable runner : registeredPostStarters) {
                log.info("| +- execute: {}: {}.", runner.getClass().getSimpleName(), runner.identifyPostStartRunner());
                runner.executePostStartRunner();
            }
            log.info("+--- all PostStartExecutables have been executed.");
        }
    }

}