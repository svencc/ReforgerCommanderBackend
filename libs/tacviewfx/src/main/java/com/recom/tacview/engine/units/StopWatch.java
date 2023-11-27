package com.recom.tacview.engine.units;

import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
public class StopWatch {

    @Getter
    private int time;

    public void tick() {
        time++;
    }

}
