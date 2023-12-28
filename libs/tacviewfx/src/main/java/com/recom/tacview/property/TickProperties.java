package com.recom.tacview.property;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties("engine.tick")
public class TickProperties {

    private int targetTps;

    private long tickThresholdNanoTime = 0;


    public long getTickThresholdNanoTime() {
        if (tickThresholdNanoTime == 0.0) {
            tickThresholdNanoTime = Duration.ofSeconds(1).toNanos() / targetTps;
        }

        return tickThresholdNanoTime;
    }

}
