package com.recom.commander.service;

import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Timer;
import java.util.TimerTask;

@Service
public class Scheduler {

    @NonNull
    private final Timer timer = new Timer(true);


    public void schedule(
            @NonNull final Runnable task,
            @NonNull final Duration delay
    ) {
        schedule(task, delay.toMillis());
    }

    public void schedule(
            @NonNull final Runnable task,
            final long delayInMillis
    ) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                task.run();
            }
        }, Math.max(0, delayInMillis));
    }

}
