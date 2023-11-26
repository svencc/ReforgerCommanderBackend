package com.recom.tacview.service.tick;

import com.recom.tacview.engine.units.StopWatch;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TickerService {

    @Getter
    @NonNull
    private final StopWatch stopWatch;

    public void tick() {
        stopWatch.tick();
    }

}
